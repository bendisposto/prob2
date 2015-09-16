package de.prob.model.eventb.algorithm.graph

import de.prob.animator.domainobjects.EventB
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
		INode restOfGraph = extractGraph(rest)
		List<BranchCondition> branches = combineBranches(body, whileStmt, whileStmt.condition)
		List<BranchCondition> branches2 = combineBranches(restOfGraph, whileStmt, whileStmt.notCondition)
		List<BranchCondition> combinedBranches = []
		combinedBranches.addAll(branches)
		combinedBranches.addAll(branches2)
		Branch b = new Branch(combinedBranches)

		List<INode> endNodes = []
		branches.each { BranchCondition cond ->
			if (cond.getOutNode() instanceof Nil) {
				body = new Node(new Assignments(whileStmt.typeEnvironment), new Nil())
				cond.setEndNode(body)
				endNodes << body
			} else {
				List<INode> end = getEndNodes(cond)
				endNodes.addAll(end)
			}
		}
		endNodes.each { INode node ->
			if (node instanceof BranchCondition) {
				body = new Node(new Assignments(whileStmt.typeEnvironment), b)
				node.setEndNode(body)
			} else {
				node.setEndNode(b)
			}
		}
		return b
	}

	def INode extractNode(If ifStmt, Iterator<Statement> rest) {
		INode yesNode = extractGraph(ifStmt.Then.statements.iterator())
		List<BranchCondition> branches = combineBranches(yesNode, ifStmt, ifStmt.condition)
		INode noNode = extractGraph(ifStmt.Else.statements.iterator())
		branches.addAll(combineBranches(noNode, ifStmt, ifStmt.elseCondition))

		INode restOfGraph = extractGraph(rest)
		branches.each { BranchCondition cond ->
			if (cond.getOutNode() instanceof Nil) {
				cond.setOutNode(restOfGraph)
			} else {
				List<INode> end = getEndNodes(cond)
				end.each { INode e ->
					e.setEndNode(restOfGraph)
				}
			}
		}
		return new Branch(branches)
	}

	def List<BranchCondition> combineBranches(INode node, Statement statement, EventB condition) {
		List<BranchCondition> branches = []
		if (node instanceof Branch) {
			node.branches.each { BranchCondition cond ->
				List<Statement> statements = [statement]
				List<EventB> conditions = [condition]
				conditions.addAll(cond.getConditions())
				statements.addAll(cond.getStatements())
				branches.add(new BranchCondition(conditions, statements, cond.getOutNode()))
			}
		} else {
			branches.add(new BranchCondition([condition], [statement], node))
		}
		branches
	}

	def List<INode> getEndNodes(INode node) {
		if (node instanceof Nil) {
			return []
		}
		if (node instanceof Node || node instanceof BranchCondition) {
			if (node.getOutNode() instanceof Nil) {
				return [node]
			}
			return getEndNodes(node.getOutNode())
		}
		if (node instanceof Branch) {
			List<INode> endNodes = []
			node.branches.each { BranchCondition cond ->
				if (cond.getOutNode() instanceof Nil) {
					endNodes.add(cond)
				} else {
					List<INode> outNodes = getEndNodes(cond.getOutNode())
					endNodes.addAll(getEndNodes(cond.getOutNode()))
				}
			}
			return endNodes
		}
		throw new IllegalArgumentException("Unknown node type: ${node.getClass()}")
	}

	def INode extractNode(Assertion assertion, Iterator<Statement> rest) {
		INode node = extractGraph(rest)
		if (node instanceof Nil) {
			node = new Node(new Assignments(assertion.typeEnvironment), new Nil())
		}
		node.addAssertion(assertion)
		return node
	}

	def INode extractNode(Assignments assignments, Iterator<Statement> rest) {
		return new Node(assignments, extractGraph(rest))
	}
}

