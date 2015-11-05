package de.prob.model.eventb.algorithm

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventBAction
import de.prob.model.eventb.EventBGuard
import de.prob.model.eventb.EventModifier
import de.prob.model.eventb.FormulaUtil
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions.Options
import de.prob.model.eventb.algorithm.ast.Assignments
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.IAssignment
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.graph.AssertionTranslator
import de.prob.model.eventb.algorithm.graph.ControlFlowGraph
import de.prob.model.eventb.algorithm.graph.Edge
import de.prob.model.eventb.algorithm.graph.GraphTransformer
import de.prob.model.eventb.algorithm.graph.PCCalculator
import de.prob.model.representation.ModelElementList

class TranslationAlgorithm implements ITranslationAlgorithm {

	GraphTransformer transformer
	ControlFlowGraph graph
	ModelElementList<Procedure> procedures
	final Set<Statement> generated = [] as Set
	Procedure procedure
	Map<Statement, Integer> pcInformation
	final String pcname
	boolean optimized

	def TranslationAlgorithm(AlgorithmGenerationOptions options, ModelElementList<Procedure> procedures, String pcname="pc") {
		this.transformer = new GraphTransformer(options)
		this.optimized = options.getOptions().contains(Options.optimize)
		this.procedures = procedures
		this.pcname = pcname
	}

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm) {
		graph = transformer.transform(new ControlFlowGraph(algorithm))
		PCCalculator pcCalc = new PCCalculator(graph, optimized)
		pcInformation = pcCalc.pcInformation
		ModelElementList<Procedure> procs = machineM.getMachine().getChildrenOfType(Procedure.class)
		this.procedure = procs.isEmpty() ? null : procs[0]

		if (procedure) {
			machineM = machineM.addComment("This machine is the implementation of procedure ${procedure.getName()}")
			def endPc = pcCalc.lastPc()
			machineM = machineM.invariant("ipc /= $endPc => apc = 0")
			machineM = machineM.invariant("ipc = $endPc => apc = 1")
			def absMachine = procedure.getAbstractMachine()
			procedure.results.each { String varName ->
				def inv = absMachine.invariants["typing_$varName"]
				def init = absMachine.getEvent("INITIALISATION").actions["init_$varName"]
				machineM = machineM.variable(varName).invariant(inv).initialisation({ action init })
			}
		}

		machineM = machineM.addComment(new AlgorithmPrettyPrinter(algorithm, procedures).prettyPrint())
		if (graph.entryNode) {
			machineM = machineM.var_block("$pcname", "$pcname : NAT", "$pcname := 0")
			machineM = new AssertionTranslator(machineM, graph, pcInformation, optimized, pcname).getMachineM()
			machineM =  addNode(machineM, graph.entryNode)
		}
		return machineM;
	}

	def MachineModifier addNode(MachineModifier machineM, final Statement stmt) {
		if (generated.contains(stmt)) {
			return machineM
		}
		generated << stmt

		if (stmt instanceof While && stmt.invariant != null) {
			machineM = machineM.invariant(graph.nodeMapping.getName(stmt) +"_inv", "$pcname = ${pcInformation[stmt]} => (${stmt.invariant.getCode()})")
		}

		def merge = (stmt instanceof While || stmt instanceof If) && optimized

		graph.outEdges(stmt).each {
			machineM = addEdge(machineM, it, stmt, merge)
		}

		if (graph.outEdges(stmt) == []) {
			def name = graph.nodeMapping.getName(stmt)
			if (stmt instanceof Assignments && !stmt.assignments.isEmpty()) {
				throw new IllegalArgumentException("Algorithm must deadlock on empty assignment")
			}
			final pcname = pcname
			final pc = pcInformation[stmt]
			machineM = machineM.event(name: name) { guard "$pcname = $pc" }
		}

		machineM
	}

	def MachineModifier addEdge(MachineModifier machineM, Edge e, Statement stmt, boolean merge) {
		String name = extractName(e)
		def node = null
		if (merge && e.to instanceof IAssignment) {
			generated << e.to
			if (!graph.outEdges(e.to).isEmpty()) {
				node = graph.outEdges(e.to).first().to
			}
		}
		final nextN = node ?: e.to

		final pcs = pcInformation
		EventModifier em = new EventModifier(new Event(name, EventType.ORDINARY, false))
				.addComment(stmt.toString())
				.guard("$pcname = ${pcs[stmt]}")
		e.conditions.each { em = em.guard(it) }
		if (stmt instanceof IAssignment) {
			em = addAssignment(em, stmt)
		} else if (merge && e.to instanceof IAssignment) {
			em = addAssignment(em, e.to)
		}
		if (pcs[nextN] != null) {
			em = em.action("$pcname := ${pcs[nextN]}")
		}
		def mm = machineM.addEvent(em.getEvent())
		return addNode(mm, nextN)
	}

	def EventModifier addAssignment(EventModifier em, Assignments a) {
		a.assignments.each {
			em = em.action(it)
		}
		em
	}

	def EventModifier addAssignment(EventModifier em, Call a) {
		Procedure procedure = procedures.getElement(a.getName())
		assert procedure
		assert procedure.arguments.size() == a.arguments.size()
		assert procedure.results.size() == a.results.size()
		FormulaUtil fuu = new FormulaUtil()
		Map<String, EventB> subs = [:]
		[
			procedure.arguments,
			a.arguments
		].transpose().each { e ->
			subs[e[0]] = e[1]
		}
		[
			procedure.results,
			a.results
		].transpose().each { e ->
			subs[e[0]] = e[1]
		}
		em = em.addComment(a.toString())
		procedure.getEvent().guards.findAll{it.getName() != "grd_apc"}.each { EventBGuard grd ->
			em = em.guard(fuu.substitute(grd.getPredicate(), subs))
		}
		procedure.getEvent().actions.findAll{it.getName() != "act_apc" }.each {EventBAction act ->
			em = em.action(fuu.substitute(act.getCode(), subs))
		}
		em
	}

	def EventModifier addAssignment(EventModifier em, Return a) {
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
			def n = "act${em.actctr + 1}"
			em = em.action(n, r +":="+v.getCode(), a.toString())
		}
		em
	}

	def String extractName(Edge e) {
		List<Statement> statements = graph.edgeMapping[e]
		if (statements.size() == 1 && statements[0] instanceof IAssignment) {
			assert e.conditions.isEmpty()
			return "${graph.nodeMapping.getName(statements[0])}"
		}
		assert e.conditions.size() == statements.size()
		[statements, e.conditions].transpose().collect { l ->
			extractName(l[0], l[1])
		}.iterator().join("_")
	}

	def String extractName(While s, EventB condition) {
		def name = graph.nodeMapping.getName(s)
		if (condition == s.condition) {
			return "enter_$name"
		}
		if (condition == s.notCondition) {
			return "exit_$name"
		}
		return "unknown_branch_$name"
	}

	def String extractName(If s, EventB condition) {
		def name = graph.nodeMapping.getName(s)
		if (condition == s.condition) {
			return "${name}_then"
		}
		if (condition == s.elseCondition) {
			return "${name}_else"
		}
		return "unknown_branch_$name"
	}
}