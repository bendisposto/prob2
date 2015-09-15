package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

public class Nil implements INode {

	def List<Assertion> assertions = []

	@Override
	public INode getOutNode() {
		return null;
	}

	@Override
	public List<Statement> getStatements() {
		return Collections.EMPTY_LIST
	}

	@Override
	public void setEndNode(final INode node) {
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return "Nil"
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
