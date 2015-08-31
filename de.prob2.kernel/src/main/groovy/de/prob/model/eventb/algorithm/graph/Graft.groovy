package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

class Graft implements INode {
	def INode outNode
	def List<Assertion> assertions = []

	def Graft(INode outNode) {
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
		return []
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
