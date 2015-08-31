package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

class Branch implements INode {
	def String condition
	def String notCondition
	def INode yesNode
	def INode noNode
	def Statement statement
	def List<Assertion> assertions = []

	def Branch(Statement statement, INode yesNode, INode noNode) {
		this.statement = statement
		this.condition = statement.condition
		this.notCondition = "not($condition)"
		this.yesNode = yesNode
		this.noNode = noNode
	}

	@Override
	def INode getOutNode() {
		return yesNode // by default, follow only one of the branches
	}

	def void setEndNode(INode node) {
		throw new IllegalArgumentException("Cannot set the end node for a branch!")
	}

	@Override
	public List<Statement> getStatements() {
		return [statement]
	}

	@Override
	public String toString() {
		return statement.toString()
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
