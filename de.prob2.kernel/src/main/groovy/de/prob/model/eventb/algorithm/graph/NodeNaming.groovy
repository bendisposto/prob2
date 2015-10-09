package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.AlgorithmASTVisitor;
import de.prob.model.eventb.algorithm.Assertion;
import de.prob.model.eventb.algorithm.Assignments;
import de.prob.model.eventb.algorithm.Assumption;
import de.prob.model.eventb.algorithm.Block;
import de.prob.model.eventb.algorithm.Call;
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Return;
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class NodeNaming extends AlgorithmASTVisitor {
	int whilectr = 0
	int ifctr = 0
	int assignctr = 0
	int assertctr = 0
	int assumectr = 0
	int callctr = 0
	int returnctr = 0
	Map<String, Statement> nodes = [:]
	Map<Statement, String> naming = [:]

	def NodeNaming(Block algorithm) {
		visit(algorithm)
	}

	@Override
	public Object visit(Assertion a) {
		def name = "assert${assertctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public Object visit(Assignments a) {
		def name = "assign${assignctr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public Object visit(Assumption a) {
		def name = "assume${assumectr++}"
		nodes[name] = a
		naming[a] = name
	}

	@Override
	public visit(If i) {
		def name = "if${ifctr++}"
		nodes[name] = i
		naming[i] = name
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
