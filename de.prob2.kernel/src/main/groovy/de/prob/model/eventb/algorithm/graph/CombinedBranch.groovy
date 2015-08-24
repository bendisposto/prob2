package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Statement

public class CombinedBranch implements INode {

	List<BranchCondition> branches

	def CombinedBranch(List<BranchCondition> branches) {
		this.branches = branches
	}

	@Override
	public INode getOutNode() {
		if (branches.isEmpty()) {
			throw new RuntimeException("No out node!")
		}
		branches[0].getOutNode()
	}

	@Override
	public List<Statement> getStatements() {
		HashSet<Statement> stmts
		branches.each { stmts.addAll(it.getStatements()) }
		stmts as List
	}

	@Override
	public void setEndNode(INode node) {
		throw new IllegalArgumentException("Cannot set the end node for a combined branch")
	}
}
