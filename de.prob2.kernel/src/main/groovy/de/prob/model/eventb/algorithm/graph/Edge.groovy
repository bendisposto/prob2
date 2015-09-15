package de.prob.model.eventb.algorithm.graph;


public class Edge {
	def Integer from
	def Integer to
	def BranchCondition condition

	def Edge(Integer from, Integer to, BranchCondition condition) {
		this.from = from
		this.to = to
		this.condition = condition
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Edge) {
			return from.equals(that.getFrom()) &&
			to.equals(that.getTo()) &&
			condition.equals(that.getCondition())
		}
		return false
	}

	@Override
	public int hashCode() {
		return from * 7 + to * 13 + rep.hashCode() * 17;
	}

	@Override
	public String toString() {
		return condition ? "--${condition.getConditions().toString}-->" : "-->"
	}
}
