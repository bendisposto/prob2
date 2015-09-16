package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Assignments
import de.prob.model.eventb.algorithm.Statement

class Node implements INode {
	Assignments assignments
	INode outNode
	def List<Assertion> assertions = []

	def Node(Assignments assignments, INode outNode) {
		this.assignments = assignments
		this.outNode = outNode
	}

	@Override
	def INode getOutNode() {
		return outNode
	}

	@Override
	public void setEndNode(INode node) {
		this.outNode = node
	}

	@Override
	public List<Statement> getStatements() {
		return [assignments];
	}

	@Override
	public String toString() {
		return assignments.toString();
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
