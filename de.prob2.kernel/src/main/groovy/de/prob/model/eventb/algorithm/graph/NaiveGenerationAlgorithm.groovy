package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.AlgorithmPrettyPrinter
import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.TranslationAlgorithm
import de.prob.model.eventb.algorithm.While

class NaiveGenerationAlgorithm extends TranslationAlgorithm {

	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation
	Set<Statement> generated = [] as Set

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm) {
		graph = new ControlFlowGraph(algorithm)
		pcInformation = new PCCalculator(graph).pcInformation
		machineM = machineM.addComment(new AlgorithmPrettyPrinter(algorithm).prettyPrint())
		if (graph.entryNode) {
			machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")
			graph.assertions.each { Statement stmt, Set<Assertion> assertions ->
				assertions.each { Assertion a ->
					machineM = machineM.invariant("pc = ${pcInformation[stmt]} => (${a.assertion.getCode()})")
				}
			}

			return addNode(machineM, graph.entryNode)
		}
		return machineM
	}

	def MachineModifier addNode(MachineModifier machineM, final Statement stmt) {
		if (generated.contains(stmt)) {
			return machineM
		}
		generated << stmt
		final pcs = pcInformation

		def found = false
		graph.outEdges(stmt).each { final Edge outEdge ->
			def name = extractName(stmt, outEdge)

			machineM = machineM.event(name: name, comment: stmt.toString()) {
				guard "pc = ${pcs[stmt]}"
				outEdge.conditions.each { guard it }
				if (stmt instanceof Assignments) {
					stmt.assignments.each { action it }
				}
				if (pcs[outEdge.to] != null) {
					action "pc := ${pcs[outEdge.to]}"
				}
			}

			machineM = addNode(machineM, outEdge.to)
		}
		if (graph.outEdges(stmt) == []) {
			def name = graph.nodeMapping.getName(stmt)
			if (stmt instanceof Assignments && !stmt.assignments.isEmpty()) {
				throw new IllegalArgumentException("Algorithm must deadlock on empty assignments block")
			}

			machineM = machineM.event(name: name) { guard "pc = ${pcs[stmt]}" }
		}

		machineM
	}

	def String extractName(Assignments s, Edge e) {
		return "${graph.nodeMapping.getName(s)}"
	}

	def String extractName(While s, Edge e) {
		if (e.getConditions() == [s.condition]) {
			return "enter_${graph.nodeMapping.getName(s)}"
		}
		if (e.getConditions() == [s.notCondition]) {
			return "exit_${graph.nodeMapping.getName(s)}"
		}
		return "${graph.nodeMapping.getName(s)}"
	}

	def String extractName(If s, Edge e) {
		if (e.getConditions() == [s.condition]) {
			return "${graph.nodeMapping.getName(s)}_then"
		}
		if (e.getConditions() == [s.elseCondition]) {
			return "${graph.nodeMapping.getName(s)}_else"
		}
		return "${graph.nodeMapping.getName(s)}"
	}
}
