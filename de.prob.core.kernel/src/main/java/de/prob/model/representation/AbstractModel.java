package de.prob.model.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;

public abstract class AbstractModel implements IEntity {

	protected StateSpace statespace;
	protected HashMap<String, Label> components;
	protected DirectedMultigraph<String, RefType> graph;

	public StateSpace getStatespace() {
		return statespace;
	}

	public HashMap<String, Label> getComponents() {
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
		if (edge == null) {
			return null;
		}

		return edge.getRelationship();
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public List<IEntity> getChildren() {
		return new ArrayList<IEntity>(components.values());
	}

	@Override
	public boolean hasChildren() {
		return !components.values().isEmpty();
	}

	public Object asType(final Class<?> className) {
		if (className.getSimpleName().equals("StateSpace")) {
			return statespace;
		}
		if (className.getSimpleName().equals("History")) {
			return new History(statespace);
		}
		throw new ClassCastException("No element of type " + className
				+ " found.");
	}

}
