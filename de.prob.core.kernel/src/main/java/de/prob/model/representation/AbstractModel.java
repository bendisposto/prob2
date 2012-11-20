package de.prob.model.representation;

import java.util.Map;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;

public abstract class AbstractModel extends AbstractElement {

	protected StateSpace statespace;
	protected DirectedMultigraph<String, RefType> graph = new DirectedMultigraph<String, RefType>(
			new ClassBasedEdgeFactory<String, RefType>(RefType.class));

	public StateSpace getStatespace() {
		return statespace;
	}

	public abstract AbstractElement getComponent(String name);

	// This is needed for the graph representation
	public abstract Map<String, AbstractElement> getComponents();

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
