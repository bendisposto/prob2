package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

class AssertNode implements INode {

	Assertion assertion
	INode node

	def AssertNode(Assertion assertion, INode outNode) {
		this.assertion = assertion
		this.node = outNode
	}


	@Override
	public INode getOutNode() {
		return node
	}

	@Override
	public List<Statement> getStatements() {
		return [assertion]
	}

	@Override
	public void setEndNode(INode node) {
		this.node = node
	}
}
