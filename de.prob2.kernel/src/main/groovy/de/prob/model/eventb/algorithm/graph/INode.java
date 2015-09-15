package de.prob.model.eventb.algorithm.graph;

import java.util.List;

import de.prob.model.eventb.algorithm.Assertion;
import de.prob.model.eventb.algorithm.Statement;

public interface INode {
	public INode getOutNode();

	public List<Statement> getStatements();

	public void setEndNode(INode node);

	public void addAssertion(Assertion assertion);

	public List<Assertion> getAssertions();
}
