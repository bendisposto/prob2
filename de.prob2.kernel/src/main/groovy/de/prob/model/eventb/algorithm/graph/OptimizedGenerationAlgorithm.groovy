package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.Event
import de.prob.model.eventb.EventModifier
import de.prob.model.eventb.FormulaUtil;
import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.Variant
import de.prob.model.eventb.Event.EventType
import de.prob.model.eventb.algorithm.AlgorithmPrettyPrinter
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.Call
import de.prob.model.eventb.algorithm.IAssignment
import de.prob.model.eventb.algorithm.ITranslationAlgorithm
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.LoopInformation
import de.prob.model.eventb.algorithm.Procedure
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While
import de.prob.model.representation.ModelElementList

class OptimizedGenerationAlgorithm implements ITranslationAlgorithm {

	ControlFlowGraph graph
	ModelElementList<Procedure> procedures
	Map<Statement, Integer> pcInformation
	final Set<Statement> generated = [] as Set
	Map<Statement, LoopInformation> loopInfo = [:]
	List<IGraphTransformer> transformers
	Map<String, Integer> assertCtr = [:]
	final String pcname

	def OptimizedGenerationAlgorithm(List<IGraphTransformer> transformers=[], String pcname) {
		this.transformers = transformers
		this.pcname = pcname
	}

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm, ModelElementList<Procedure> procedures) {
		graph = transformers.inject(new ControlFlowGraph(algorithm)) { ControlFlowGraph g, IGraphTransformer t ->
			t.transform(g)
		}
		this.procedures = procedures
		pcInformation = new PCCalculator(graph, true).pcInformation
		machineM = machineM.addComment(new AlgorithmPrettyPrinter(algorithm, procedures).prettyPrint())
		if (graph.entryNode) {
			machineM = machineM.var_block("$pcname", "$pcname : NAT", "$pcname := 0")
			machineM = new AssertionTranslator(machineM, graph, pcInformation, true, pcname).getMachineM()
			machineM =  addNode(machineM, graph.entryNode)
			def loops = []
			loopInfo.each { k, v ->
				loops << v
			}
			def loopI = new ModelElementList<LoopInformation>(loops)
			machineM = new MachineModifier(machineM.getMachine().set(LoopInformation.class, loopI), machineM.typeEnvironment)
		}
		return machineM
	}

	def MachineModifier addNode(MachineModifier machineM, final Statement stmt) {
		if (generated.contains(stmt)) {
			return machineM
		}
		generated << stmt
		final pcs = pcInformation
		final pcname = pcname

		if (stmt instanceof While && stmt.invariant != null) {
			machineM = machineM.invariant(graph.nodeMapping.getName(stmt) +"_inv", "$pcname = ${pcs[stmt]} => (${stmt.invariant.getCode()})")
		}

		def branch = stmt instanceof While || stmt instanceof If

		graph.outEdges(stmt).each { final Edge outEdge ->
			def name = extractName(outEdge)

			def node = null
			if (branch && outEdge.to instanceof IAssignment) {
				generated << outEdge.to
				if (!graph.outEdges(outEdge.to).isEmpty()) {
					node = graph.outEdges(outEdge.to).first().to
				}
			}
			final nextN = node ?: outEdge.to

			EventModifier em = new EventModifier(new Event(name, EventType.ORDINARY, false))
					.addComment(stmt.toString())
					.guard("$pcname = ${pcs[stmt]}")
			outEdge.conditions.each { em = em.guard(it) }
			if (stmt instanceof IAssignment) {
				em = addAssignment(em, stmt)
			} else if (branch && outEdge.to instanceof IAssignment) {
				em = addAssignment(em, outEdge.to)
			}
			if (pcs[nextN] != null) {
				em = em.action("$pcname := ${pcs[nextN]}")
			}
			machineM = machineM.addEvent(em.getEvent())

			machineM = addNode(machineM, nextN)
			if (outEdge.loopToWhile) {
				addLoopInfo(outEdge, machineM.getMachine().getEvent(name))
			}
		}

		if (graph.outEdges(stmt) == []) {
			def name = graph.nodeMapping.getName(stmt)
			if (stmt instanceof Assignments && !stmt.assignments.isEmpty()) {
				throw new IllegalArgumentException("Algorithm must deadlock on empty assignments block")
			}

			machineM = machineM.event(name: name) { guard "$pcname = ${pcs[stmt]}" }
		}

		machineM
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
		assert procedure.result.size() == a.results.size()
		FormulaUtil fuu = new FormulaUtil()
		Map<String, EventB> subs = [:]
		[
			procedure.arguments.values() as List,
			a.arguments
		].transpose().each { e ->
			subs[e[0].getCode()] = e[1]
		}
		[
			procedure.result.values() as List,
			a.results
		].transpose().each { e ->
			subs[e[0].getCode()] = e[1]
		}
		em = em.guard(fuu.substitute(procedure.getPrecondition(), subs))
		def n = "act${em.actctr + 1}"
		em.action(n, fuu.substitute(procedure.getAbstraction(), subs), a.toString())
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

	def addLoopInfo(Edge edge, Event loopEvent) {
		def w = edge.to
		def name = graph.nodeMapping.getName(w)
		assert w instanceof While
		if (loopInfo[w] == null && w.variant != null) {
			loopInfo[w] = new LoopInformation(name, w, new Variant(w.variant, name), [])
		} else if (loopInfo[w] != null && !loopInfo[w].loopStatements.contains(loopEvent)) {
			loopInfo[w] = loopInfo[w].add(loopEvent)
		}
	}
}
