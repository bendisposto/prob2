package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Block
import de.prob.model.eventb.algorithm.If
import de.prob.model.eventb.algorithm.Statement
import de.prob.model.eventb.algorithm.While


class AlgorithmToGraph {

	INode node

	def AlgorithmToGraph(Block algorithmBlock) {
		node = extractGraph(algorithmBlock.statements.iterator())
	}

	def INode extractGraph(Iterator<Statement> stmts) {
		if (stmts.hasNext()) {
			return extractNode(stmts.next(), stmts)
		}
		return new Nil()
	}

	def INode extractNode(While whileStmt, Iterator<Statement> rest) {
		INode body = extractGraph(whileStmt.block.statements.iterator())
		Branch b

		if (body instanceof Nil) {
			body = new Node([], new Nil())
			b = new Branch(whileStmt, body, extractGraph(rest))
			body.setEndNode(b)
		} else {
			INode endN = getEndNode(body)
			b = new Branch(whileStmt, body, extractGraph(rest))
			endN.setEndNode(b)
		}

		return b
	}

	def INode extractNode(If ifStmt, Iterator<Statement> rest) {
		INode yesNode = extractGraph(ifStmt.Then.statements.iterator())
		INode noNode = extractGraph(ifStmt.Else.statements.iterator())

		INode g = extractGraph(rest)
		if (yesNode instanceof Nil) {
			yesNode = g
		} else {
			INode end1 = getEndNode(yesNode)
			end1.setEndNode(g)
		}

		if (noNode instanceof Nil) {
			noNode = g
		} else {
			INode end2 = getEndNode(noNode)
			end2.setEndNode(g)
		}

		return new Branch(ifStmt, yesNode, noNode)
	}

	def INode getEndNode(INode node) {
		def n = node
		while (!(n.getOutNode() instanceof Nil)) {
			n = n.getOutNode()
		}
		return n
	}

	def INode extractNode(Assertion assertion, Iterator<Statement> rest) {
		INode node = extractGraph(rest)
		node.addAssertion(assertion)
		return node
	}

	def INode extractNode(Assignments assignments, Iterator<Statement> rest) {
		return new Node([assignments], extractGraph(rest))
	}
}

