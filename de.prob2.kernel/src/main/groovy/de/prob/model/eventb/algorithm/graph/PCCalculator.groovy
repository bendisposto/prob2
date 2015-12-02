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
	boolean optimized

	def PCCalculator(ControlFlowGraph graph, boolean optimized) {
		this.graph = graph
		this.optimized = optimized
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
		forSingleSimpleStatement(s)
	}

	def visit(Call s) {
		forSingleSimpleStatement(s)
	}

	public visit(Return s) {
		forSingleSimpleStatement(s)
	}

	@Override
	public Object visit(Skip a) {
		if (graph.nodes.contains(a)) {
			pcInformation[a] = pc++
		}
	}

	def forSingleSimpleStatement(Statement s) {
		if (!optimized && graph.nodes.contains(s)) {
			pcInformation[s] = pc++
		} else if(optimized && graph.entryNode == s) {
			pcInformation[s] = pc++
		} else if(optimized && !graph.inEdges(s).findAll { Edge e -> e.conditions.isEmpty() }.isEmpty()) {
			pcInformation[s] = pc++
		}
	}

	def visit(Assertion s) {
		// do nothing
	}

	def int lastPc() {
		return pc - 1
	}
}
