package de.prob.model.eventb.algorithm.graph;


public class Edge {
	def Integer from
	def Integer to
	def String rep

	def Edge(Integer from, Integer to, String rep) {
		this.from = from
		this.to = to
		this.rep = rep
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Edge) {
			return from.equals(that.getFrom()) &&
			to.equals(that.getTo()) &&
			rep.equals(that.getRep())
		}
		return false
	}

	@Override
	public int hashCode() {
		return from * 7 + to * 13 + rep.hashCode() * 17;
	}

	@Override
	public String toString() {
		return "${from}${rep}${to}"
	}
}
