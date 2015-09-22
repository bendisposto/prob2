package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.MachineModifier
import de.prob.model.eventb.algorithm.AlgorithmPrettyPrinter
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
	int evtctr = 0

	@Override
	public MachineModifier run(MachineModifier machineM, Block algorithm) {
		graph = new ControlFlowGraph(algorithm)
		pcInformation = new PCCalculator(graph).pcInformation
		machineM = machineM.addComment(new AlgorithmPrettyPrinter(algorithm).prettyPrint())
		if (graph.entryNode) {
			machineM = machineM.var_block("pc", "pc : NAT", "pc := 0")
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

			machineM = machineM.event(name: name) {
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

		machineM
	}

	def String extractName(Assignments s, Edge e) {
		return "evt${evtctr++}_${graph.nodeMapping.getName(s)}"
	}

	def String extractName(While s, Edge e) {
		if (e.getConditions() == [s.condition]) {
			return "evt${evtctr++}_enter_${graph.nodeMapping.getName(s)}"
		}
		if (e.getConditions() == [s.notCondition]) {
			return "evt${evtctr++}_exit_${graph.nodeMapping.getName(s)}"
		}
		return "evt${evtctr++}_${graph.nodeMapping.getName(s)}"
	}

	def String extractName(If s, Edge e) {
		if (e.getConditions() == [s.condition]) {
			return "evt${evtctr++}_${graph.nodeMapping.getName(s)}_then"
		}
		if (e.getConditions() == [s.elseCondition]) {
			return "evt${evtctr++}_${graph.nodeMapping.getName(s)}_else"
		}
		return "evt${evtctr++}_${graph.nodeMapping.getName(s)}"
	}
}
