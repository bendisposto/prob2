package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion;
import de.prob.model.eventb.algorithm.Assignments;
import de.prob.model.eventb.algorithm.Block;
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While

class NodeNaming {
	int whilectr = 0
	int ifctr = 0
	int assignctr = 0
	int assertctr = 0
	Map<String, Statement> nodes = [:]
	Map<Statement, String> naming = [:]

	def NodeNaming(Block algorithm) {
		addBlock(algorithm)
	}

	def addBlock(Block block) {
		block.statements.each { addStatement(it) }
	}

	def addStatement(While w) {
		def name = "while${whilectr++}"
		nodes[name] = w
		naming[w] = name
		addBlock(w.block)
	}

	def addStatement(If i) {
		def name = "if${ifctr++}"
		nodes[name] = i
		naming[i] = name
		addBlock(i.Then)
		addBlock(i.Else)
	}

	def addStatement(Assignments a) {
		def name = "assign${assignctr++}"
		nodes[name] = a
		naming[a] = name
	}

	def addStatement(Assertion a) {
		def name = "assert${assertctr++}"
		nodes[name] = a
		naming[a] = name
	}

	def Statement getNode(String name) {
		nodes[name]
	}

	def String getName(Statement node) {
		naming[node]
	}
}
