package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While


class PCCalculator extends AlgorithmASTVisitor {
	ControlFlowGraph graph
	Map<Statement, Integer> pcInformation
	int pc = 0

	def PCCalculator(ControlFlowGraph graph) {
		this.graph = graph
		pcInformation = [:]
		visit(graph.algorithm)
	}

	def visit(While s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	def visit(If s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	def visit(Assignment s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	def visit(Call s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	public visit(Return s) {
		if (graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		}
	}

	@Override
	public Object visit(Skip a) {
		if (graph.nodes.contains(a)) {
			pcInformation[a] = pc++
		}
	}

	def visit(Assertion s) {
		// do nothing
	}

	def int lastPc() {
		return pc - 1
	}
}
