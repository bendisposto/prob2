package de.prob.model.representation;

import java.io.File;
import java.util.Map;

import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public abstract class AbstractModel extends AbstractElement {

	protected StateSpace statespace;
	protected File modelFile;
	protected DirectedSparseMultigraph<String, RefType> graph = new DirectedSparseMultigraph<String, RefType>();

	public StateSpace getStatespace() {
		return statespace;
	}

	public abstract AbstractElement getComponent(String name);

	// This is needed for the graph representation
	public abstract Map<String, AbstractElement> getComponents();

	public DirectedSparseMultigraph<String, RefType> getGraph() {
		return graph;
	}

	public ERefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public ERefType getEdge(final String comp1, final String comp2) {
		final RefType edge = graph.findEdge(comp1, comp2);
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

	public abstract StateSchema getStateSchema();

	public abstract AbstractElement getMainComponent();

	public File getModelFile() {
		return modelFile;
	}
}
