package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Statement

class Node implements INode {
	List<Statement> actions
	INode outNode

	def Node(List<Statement> actions, INode outNode) {
		this.actions = actions
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
		return actions;
	}

	@Override
	public String toString() {
		return actions.toString();
	}
}
