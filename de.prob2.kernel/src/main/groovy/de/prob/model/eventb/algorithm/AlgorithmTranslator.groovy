package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBAction
import de.prob.model.eventb.EventBGuard
import de.prob.model.eventb.EventBMachine
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.EventModifier
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.ModelModifier
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.transform.AddLoopEvents
import de.prob.model.eventb.algorithm.ast.transform.DeadCodeRemover
import de.prob.model.eventb.algorithm.ast.transform.IAlgorithmASTTransformer
import de.prob.model.eventb.algorithm.graph.AssertionTranslator
import de.prob.model.eventb.algorithm.graph.ControlFlowGraph
import de.prob.model.eventb.algorithm.graph.Edge
import de.prob.model.eventb.algorithm.graph.GraphTransformer
import de.prob.model.eventb.algorithm.graph.NodeNaming
import de.prob.model.eventb.algorithm.graph.PCCalculator
import de.prob.model.eventb.algorithm.graph.VariantAssertionTranslator
import de.prob.model.eventb.algorithm.graph.VariantOrdering
import de.prob.model.representation.ModelElementList

class AlgorithmTranslator {
	def ModelModifier modelM
	def AlgorithmGenerationOptions options
	def ModelElementList<Procedure> procedures

	def AlgorithmTranslator(EventBModel model, AlgorithmGenerationOptions options) {
		this.modelM = new ModelModifier(model)
		this.procedures = model.getChildrenOfType(Procedure.class)
		this.options = options
	}

	def AlgorithmTranslator(ModelModifier modelM, AlgorithmGenerationOptions options) {
		this.modelM = modelM
		this.options = options
	}

	def EventBModel run() {
		modelM.getModel().getMachines().collect { it.getName() }.each {
			modelM = runTranslation(modelM, it)
		}
		modelM.getModel()
	}

	def ModelModifier runTranslation(ModelModifier modelM, String name) {
		EventBMachine oldM = modelM.getModel().getMachine(name)
		def procedure = getProcedure(oldM)
		if (procedure && oldM.getName().endsWith(Procedure.ABSTRACT_SUFFIX)) {
			return finishAbstractProcedure(modelM, oldM)
		}

		Block algorithm = extractAlgorithm(oldM)
		if (algorithm) {
			ControlFlowGraph graph = new GraphTransformer(options).transform(new ControlFlowGraph(algorithm))
			EventBMachine newM = translateAlgorithm(oldM, graph)
			modelM = modelM.replaceMachine(oldM, newM)
			if (newM.getVariant()) {
				modelM = runAlgorithmTerminationAnalysis(modelM, name, graph)
			} else if (options.isTerminationAnalysis()) {
				modelM = runLoopTerminationAnalysis(modelM, name, graph)
			}
		}
		return modelM
	}

	def Procedure getProcedure(EventBMachine machine) {
		def List<Procedure> procs = machine.getChildrenOfType(Procedure.class)
		return procs ? procs[0] : null
	}

	def ModelModifier finishAbstractProcedure(ModelModifier modelM, EventBMachine oldM) {
		List<Procedure> proc = oldM.getChildrenOfType(Procedure.class)
		if (!proc) {
			return modelM
		}

		Event e = proc[0].getEvent()
		if (proc[0].getImplementation().getVariant()) {
			// if the implementation has a defined variant, the event in the refinement needs to be set to anticipated
			e = e.changeType(EventType.ANTICIPATED)
		}

		modelM.machine(name: oldM.getName()) { addEvent(e) }
	}

	def Block extractAlgorithm(EventBMachine machine) {
		List<Block> blocks = machine.getChildrenOfType(Block.class)
		if (blocks.size() == 1) {
			return runASTTransformations(blocks[0])
		}
		return null
	}

	def EventBMachine translateAlgorithm(EventBMachine oldM, ControlFlowGraph graph) {
		MachineModifier machineM = new MachineModifier(oldM, modelM.typeEnvironment)
		PCCalculator pcCalc = new PCCalculator(graph)
		Procedure procedure = getProcedure(oldM)

		if (procedure) {
			machineM = machineM.addComment("This machine is the implementation of procedure ${procedure.getName()}")
			def endPc = pcCalc.lastPc()
			machineM = machineM.invariant("ipc < $endPc => apc = 0")
			machineM = machineM.invariant("ipc >= $endPc => apc = 1")
			def absMachine = procedure.getAbstractMachine()

			procedure.results.each { String varName ->
				def inv = absMachine.invariants["typing_$varName"]
				def init = absMachine.getEvent("INITIALISATION").actions["init_$varName"]
				machineM = machineM.variable(varName).invariant(inv).initialisation({ action init })
			}
		}

		def pcname = procedure ? "ipc" : "pc"
		machineM = machineM.addComment(new AlgorithmPrettyPrinter(graph.getAlgorithm(), procedures).prettyPrint())
		if (graph.entryNode) {
			machineM = machineM.var("$pcname", "$pcname : NAT", "$pcname := 0")
			machineM = new AssertionTranslator(machineM, procedures, graph, pcCalc.pcInformation, options, pcname).getMachineM()
			machineM =  addNode([] as Set, graph, machineM, procedure, graph.entryNode, pcCalc.pcInformation)
		}
		if (machineM.getMachine().getVariant()) {
			machineM.getMachine().getEvents().each { Event e ->
				if (e.getName() != "INITIALISATION"){
					machineM = machineM.event(name: e.getName(), type: EventType.ANTICIPATED)
				}
			}
		} else if (options.isTerminationAnalysis()) {
			NodeNaming n = new NodeNaming(graph.algorithm)
			graph.loopsToWhile().findAll { stmt, edges -> stmt.variant != null}.each { While stmt, Set<Edge> edges ->
				edges.each { edge ->
					machineM = machineM.event(name: edge.getName(n), type: EventType.ANTICIPATED)
				}
			}
		}

		machineM.getMachine()
	}

	def MachineModifier addNode(Set<Statement> generated, ControlFlowGraph graph,  MachineModifier machineM, Procedure procedure, final Statement stmt, Map<Statement, Integer> pcInfo) {
		if (generated.contains(stmt)) {
			return machineM
		}
		generated << stmt

		NodeNaming n = new NodeNaming(graph.algorithm)
		final pcname = procedure ? "ipc" : "pc"
		final pcs = pcInfo

		List<Edge> oEdges = graph.outEdges(stmt).sort { Edge a, Edge b -> pcs[a.to] <=> pcs[b.to] }
		oEdges.each { final Edge e ->
			String eventName = e.getName(n)
			EventModifier em = new EventModifier(new Event(eventName, EventType.ORDINARY, false))
					.addComment(e.rep())
					.guard("$pcname = ${pcs[stmt]}")
			e.conditions.each { em = em.guard(it.getSecond()) }
			if (e.assignment) {
				em = addAssignment(em, e.assignment, procedure)
			}
			em = em.action("$pcname := ${pcs[e.to]}")
			machineM = machineM.addEvent(em.getEvent())
			machineM = addNode(generated, graph, machineM, procedure, e.to, pcInfo)
		}

		if (graph.outEdges(stmt) == [] as Set) {
			def name = n.getName(stmt)
			if (!(stmt instanceof Skip)) {
				throw new IllegalArgumentException("Algorithm must deadlock on empty assignment")
			}
			final pc = pcInfo[stmt]

			if (machineM.getMachine().getVariant()) {
				machineM = machineM.event(name: name) {
					guard "$pcname = $pc"
					action "$pcname := ${pc + 1}"
				}
			} else {
				machineM = machineM.event(name: name) { guard "$pcname = $pc" }
			}
		}

		machineM
	}

	def EventModifier addAssignment(EventModifier em, Assignment a, Procedure procedure) {
		em.action(a.assignment)
	}

	def EventModifier addAssignment(EventModifier em, Call a, Procedure p) {
		Procedure procedure = procedures.getElement(a.getName())
		FormulaUtil fuu = new FormulaUtil()
		Map<String, EventB> subs = a.getSubstitutions(procedure)
		em = em.addComment(a.toString())
		procedure.getEvent().guards.findAll{it.getName() != "grd_apc"}.each { EventBGuard grd ->
			em = em.guard(fuu.substitute(grd.getPredicate(), subs))
		}
		procedure.getEvent().actions.findAll{it.getName() != "act_apc" }.each {EventBAction act ->
			em = em.action(fuu.substitute(act.getCode(), subs))
		}
		em
	}

	def EventModifier addAssignment(EventModifier em, Return a, Procedure procedure) {
		if (procedure == null) {
			throw new IllegalArgumentException("Return statements are only allowed within procedure definitions!")
		}
		assert procedure.results.size() == a.returnVals.size()

		em = em.addComment(a.toString())
		em = em.refines(procedure.getEvent(), false)
		[
			procedure.results,
			a.returnVals
		].transpose().each { String r, EventB v ->
			em = em.action("act0", r +":="+v.getCode(), a.toString())
		}
		em
	}

	def Block runASTTransformations(Block block) {
		def transformers = [
			new DeadCodeRemover() ,
			new AddLoopEvents(options)
		]
		transformers.inject(block) { Block b, IAlgorithmASTTransformer t ->
			t.transform(b)
		}
	}

	def ModelModifier runLoopTerminationAnalysis(ModelModifier modelM, String mchName, ControlFlowGraph graph) {
		PCCalculator pcCalc = new PCCalculator(graph)
		def pcname = getProcedure(modelM.getModel().getMachine(mchName)) ? "ipc" : "pc"

		def ordering = new VariantOrdering()
		ordering.visit(graph.algorithm)
		def refName = mchName
		def ctr = 0
		NodeNaming n = new NodeNaming(graph.algorithm)
		ordering.ordering.each { While stmt ->
			def newName = "${mchName}_term${ctr++}_"+n.getName(stmt)
			modelM = modelM.refine(refName, newName)
			def oldM = modelM.getModel().getMachine(newName)
			def mM = new MachineModifier(oldM)
			def newM = createLoopTermination(mM, stmt, graph, pcCalc, pcname)
			refName = newName
			modelM = modelM.replaceMachine(oldM, newM)
		}
		modelM
	}

	def EventBMachine createLoopTermination(MachineModifier machineM, While stmt, ControlFlowGraph graph, PCCalculator pcCalc, String pcname) {
		NodeNaming n = new NodeNaming(graph.algorithm)
		String variantName = n.getName(stmt)+"_variant"
		def mM = machineM.variant(variantName).var(variantName, variantName + " : NAT", variantName + " :: NAT")
		def final setVariant = variantName+" := ${stmt.variant.getCode()}"
		def prefix = "enter_"+n.getName(stmt)
		mM.getMachine().getEvents().findAll { it.getName().startsWith(prefix) }.each { Event evt ->
			mM = mM.event(name: evt.getName(), type: evt.getType()) { action setVariant }
		}

		graph.loopsToWhile()[stmt].each { Edge edge ->
			mM = mM.event(name: edge.getName(n), type: EventType.CONVERGENT) { action setVariant }
		}
		mM = mM.initialisation(extended: true)
		mM = new VariantAssertionTranslator(mM, stmt, procedures, graph, pcCalc.pcInformation, options, pcname).getMachineM()
		mM.getMachine()
	}

	def ModelModifier runAlgorithmTerminationAnalysis(ModelModifier modelM, String mchName, ControlFlowGraph graph) {
		PCCalculator pcCalc = new PCCalculator(graph)
		def pcname = getProcedure(modelM.getModel().getMachine(mchName)) ? "ipc" : "pc"

		EventBMachine mch = modelM.getModel().getMachine(mchName)
		MachineModifier mM = new MachineModifier(mch)
		NodeNaming n = new NodeNaming(graph.algorithm)
		graph.loopsToWhile().each { While stmt, Set<Edge> edges ->
			edges.each { edge ->
				mM = mM.event(name:  edge.getName(n), type: EventType.CONVERGENT)
			}
		}
		modelM = modelM.replaceMachine(mch, mM.getMachine())
		def newName = "${mchName}_termination"
		modelM = modelM.refine(mchName, newName)
		mch = modelM.getModel().getMachine(newName)
		mM = new MachineModifier(mch)
		mM = mM.variant("${pcCalc.lastPc() + 1} - $pcname")
		mM.getMachine().getEvents().findAll { it.getType() == EventType.ANTICIPATED }.each { Event evt ->
			mM = mM.replaceEvent(evt, evt.changeType(EventType.CONVERGENT))
		}
		modelM = modelM.replaceMachine(mch, mM.getMachine())
	}
}
