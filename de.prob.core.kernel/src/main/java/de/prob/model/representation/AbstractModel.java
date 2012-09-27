package de.prob.model.representation;

import java.util.HashMap;

import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.StateSpace;

public abstract class AbstractModel {

	protected StateSpace statespace;
	protected HashMap<String, AbstractElement> components;
	protected DirectedMultigraph<String, RefType> graph;

	public StateSpace getStatespace() {
		return statespace;
	}

	public HashMap<String, AbstractElement> getComponents() {
		return components;
	}

	public DirectedMultigraph<String, RefType> getGraph() {
		return graph;
	}

	public ERefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public ERefType getEdge(final String comp1, final String comp2) {
		final RefType edge = graph.getEdge(comp1, comp2);
		if (edge == null)
			return null;

		return edge.getRelationship();
	}

	@Override
	public String toString() {
		return graph.toString();
	}
}
