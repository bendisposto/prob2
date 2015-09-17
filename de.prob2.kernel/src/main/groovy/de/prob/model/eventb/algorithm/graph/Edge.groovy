package de.prob.model.eventb.algorithm.graph;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.algorithm.Statement


public class Edge {
	def String from
	def String to
	def List<EventB> conditions
	def List<String> statements
	def Map<String, Integer> pcInformation

	def Edge(String from, String to, List<EventB> conditions, List<String> statements, Map<String, Integer> pcInformation) {
		this.from = from
		this.to = to
		this.conditions = conditions
		this.statements = statements
		this.pcInformation = pcInformation
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Edge) {
			return from.equals(that.getFrom()) &&
					to.equals(that.getTo()) &&
					conditions.equals(that.getConditions()) &&
					statements.equals(that.getStatements())
		}
		return false
	}

	@Override
	public int hashCode() {
		return from * 7 + to * 13 + conditions.hashCode() * 17 + statements.hashCode() * 19;
	}

	@Override
	public String toString() {
		return conditions ? "--${conditions.toString}-->" : "-->"
	}
}
