package de.prob.model.eventb.algorithm.graph;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.ast.Statement;


public class Edge {
	def Statement from
	def Statement to
	def List<EventB> conditions
	def final boolean loopToWhile

	def Edge(Statement from, Statement to, List<EventB> conditions, loopToWhile=false) {
		this.from = from
		this.to = to
		this.conditions = conditions
		this.loopToWhile = loopToWhile
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Edge) {
			return from.equals(that.getFrom()) &&
					to.equals(that.getTo()) &&
					conditions.equals(that.getConditions())
		}
		return false
	}

	@Override
	public int hashCode() {
		return from.hashCode() * 7 + to.hashCode() * 13 + conditions.hashCode() * 17;
	}

	@Override
	public String toString() {
		return conditions ? "--${conditions.toString()}-->" : "-->"
	}
}
