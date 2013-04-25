package de.prob.statespace;

import java.util.Collection;
import java.util.HashMap;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class StateSpaceGraph {

	private final DirectedSparseMultigraph<StateId, OpInfo> graph;
	protected final HashMap<String, StateId> states = new HashMap<String, StateId>();
	public final StateId __root;

	public StateSpaceGraph(final DirectedSparseMultigraph<StateId, OpInfo> graph) {
		this.graph = graph;
		__root = new StateId("root", this);
		addVertex(__root);
	}

	/**
	 * Returns the StateId for the given key
	 * 
	 * @param key
	 * @return StateId for the specified key
	 */
	public StateId getVertex(final String key) {
		return states.get(key);
	}

	public DirectedSparseMultigraph<StateId, OpInfo> getGraph() {
		return graph;
	}

	public int outDegree(final StateId arg0) {
		return graph.outDegree(arg0);
	}

	public Collection<OpInfo> getOutEdges(final StateId arg0) {
		return graph.getOutEdges(arg0);
	}

	public boolean addEdge(final OpInfo with, final StateId from,
			final StateId to) {
		return graph.addEdge(with, from, to);
	}

	public boolean addVertex(final StateId arg0) {
		states.put(arg0.getId(), arg0);
		return graph.addVertex(arg0);
	}

	public boolean containsEdge(final OpInfo arg0) {
		return graph.containsEdge(arg0);
	}

	public boolean containsVertex(final StateId arg0) {
		return graph.containsVertex(arg0);
	}

	public Collection<OpInfo> getEdges() {
		return graph.getEdges();
	}

	public StateId getDest(final OpInfo arg0) {
		return graph.getDest(arg0);
	}

	public Collection<StateId> getVertices() {
		return graph.getVertices();
	}

	public boolean isOutEdge(final StateId sId, final OpInfo oId) {
		return getOutEdges(sId).contains(oId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		Collection<StateId> vertexSet = getVertices();
		sb.append("(");
		sb.append(vertexSet.toString());
		sb.append(", ");
		Collection<OpInfo> edgeSet = getEdges();
		sb.append(edgeSet.toString() + ")");
		return sb.toString();
	}

}
