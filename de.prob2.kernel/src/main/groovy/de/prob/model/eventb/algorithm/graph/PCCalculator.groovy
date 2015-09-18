package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Statement

class PCCalculator {
	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation
	int pc = 0

	def PCCalculator(ControlFlowGraph graph) {
		this.graph = graph
		pcInformation = [:]
		if (graph.entryNode) {
			addNode(graph.entryNode)
		}
	}

	def addNode(Statement stmt) {
		if (pcInformation[stmt] != null) {
			return
		}
		pcInformation[stmt] = pc++
		graph.outEdges(stmt).each { Edge e ->
			addNode(e.to)
		}
	}
}
