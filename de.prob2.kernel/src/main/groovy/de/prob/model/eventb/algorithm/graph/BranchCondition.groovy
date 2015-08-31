package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.Assertion
import de.prob.model.eventb.algorithm.Statement

public class BranchCondition implements INode {
	def List<String> conditions
	def List<Statement> statements
	def INode outNode
	def List<Assertion> assertions = []

	def BranchCondition(List<String> conditions, List<Statement> statements, INode outNode) {
		this.conditions = conditions
		this.statements = statements
		this.outNode = outNode
	}

	@Override
	public String toString() {
		return conditions.toString()
	}

	@Override
	public INode getOutNode() {
		outNode
	}

	@Override
	public List<Statement> getStatements() {
		return statements
	}

	@Override
	public void setEndNode(INode node) {
		this.outNode = node
	}

	@Override
	public void addAssertion(Assertion assertion) {
		assertions.add(assertion)
	}
}
