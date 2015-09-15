package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

public class Branch implements INode {

	List<BranchCondition> branches
	def List<Assertion> assertions = []

	def Branch(List<BranchCondition> branches) {
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
		HashSet<Statement> stmts  = new HashSet<Statement>()
		branches.each { stmts.addAll(it.getStatements()) }
		stmts as List
	}

	@Override
	public void setEndNode(INode node) {
		throw new IllegalArgumentException("Cannot set the end node for a combined branch")
	}

	@Override
	public String toString() {
		return getStatements().toString()
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
