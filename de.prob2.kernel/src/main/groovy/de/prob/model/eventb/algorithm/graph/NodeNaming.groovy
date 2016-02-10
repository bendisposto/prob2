package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.ast.AlgorithmASTVisitor
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Assignment
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Call
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class NodeNaming extends AlgorithmASTVisitor {
	int whilectr = 0
	int ifctr = 0
	int assignctr = 0
	int assertctr = 0
	int assumectr = 0
	int callctr = 0
	int returnctr = 0
	Map<String, Statement> nodes = [:]
	Map<Statement, String> naming = [(ControlFlowGraph.FILLER): "filler",
		(ControlFlowGraph.END): "end"]

	def Skip start
	def Skip end

	def blockScope = null

	def NodeNaming(Block algorithm) {
		if (algorithm.statements) {
			start = algorithm.statements.first() instanceof Skip ? algorithm.statements.first() : null
			end = algorithm.statements.last() instanceof Skip ? algorithm.statements.last() : null
		}
		visit(algorithm)
	}

	@Override
	public Object visit(Assertion a) {
		def name = "assert${assertctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public Object visit(Assignment a) {
		def name = "assign${assignctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public visit(If i) {
		def name = "if${ifctr++}"
		nodes[name] = i
		naming[i] = name
	}

	public traverse(While s) {
		def oldS = blockScope
		visit(s)
		blockScope = "loop_to_"+ naming[s]
		visit(s.block)
		blockScope = oldS
	}

	public traverse(If i) {
		def oldS = blockScope
		visit(i)
		blockScope = naming[i] + "_then_end"
		visit(i.Then)
		blockScope = naming[i] + "_else_end"
		visit(i.Else)
		blockScope = oldS
	}

	@Override
	public visit(While w) {
		def name = "while${whilectr++}"
		nodes[name] = w
		naming[w] = name
	}

	@Override
	public visit(Call a) {
		def name = "call${callctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public visit(Return a) {
		def name = "return${returnctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public Object visit(Skip a) {
		def name = null
		if (a == end) {
			name = "end_algorithm"
		} else if (a == start) {
			name = "enter_algorithm"
		} else {
			name = blockScope
		}
		assert name != null
		nodes[name] = a
		naming[a] = name
	}

	def Statement getNode(String name) {
		nodes[name]
	}

	def String getName(Statement node) {
		naming[node]
	}

	def getAt(String name) {
		nodes[name]
	}
}
