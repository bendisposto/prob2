package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Statement

class Graft implements INode {
	def INode in1
	def INode in2
	def INode outNode

	def Graft(INode in1, INode in2, INode outNode) {
		this.in1 = in1
		this.in2 = in2
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
}
