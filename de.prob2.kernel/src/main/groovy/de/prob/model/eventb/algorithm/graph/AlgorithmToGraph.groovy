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
		List<BranchCondition> branches = combineBranches(yesNode, ifStmt, ifStmt.condition)
		INode noNode = extractGraph(ifStmt.Else.statements.iterator())
		branches.addAll(combineBranches(noNode, ifStmt, "not(${ifStmt.condition})"))

		INode restOfGraph = extractGraph(rest)
		branches.each { BranchCondition cond ->
			if (cond.getOutNode() instanceof Nil) {
				cond.setOutNode(restOfGraph)
			} else {
				INode end = getEndNode(cond)
				end.setEndNode(restOfGraph)
			}
		}
		return new CombinedBranch(branches)
	}

	def List<BranchCondition> combineBranches(INode node, Statement statement, String condition) {
		List<BranchCondition> branches = []
		if (node instanceof CombinedBranch) {
			node.branches.each {
				List<Statement> statements = [statement]
				List<String> conditions = [condition]
				conditions.addAll(node.getConditions())
				statements.addAll(node.getStatements())
				branches.add(new BranchCondition(conditions, statements, b.getOutNode()))
			}
		} else {
			branches.add(new BranchCondition([condition], [statement], node))
		}
		branches
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

