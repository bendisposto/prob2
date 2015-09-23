package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class PCCalculator {
	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation
	int pc = 0

	def PCCalculator(ControlFlowGraph graph) {
		this.graph = graph
		pcInformation = [:]
		addBlock(graph.woAssertions)
	}

	def addBlock(Block block) {
		block.statements.each { Statement stmt ->
			addStatement(stmt)
		}
	}

	def addStatement(While s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
		addBlock(s.block)
	}

	def addStatement(If s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
		addBlock(s.Then)
		addBlock(s.Else)
	}

	def addStatement(Assignments s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	def addStatement(Assertion s) {
		// do nothing
	}
}
