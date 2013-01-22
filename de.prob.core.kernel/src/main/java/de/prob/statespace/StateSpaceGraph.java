package de.prob.statespace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;

import de.prob.animator.domainobjects.OpInfo;

public class StateSpaceGraph {

	private final DirectedMultigraph<StateId, OpInfo> graph;

	public StateSpaceGraph(final DirectedMultigraph<StateId, OpInfo> graph) {
		this.graph = graph;
	}

	public DirectedMultigraph<StateId, OpInfo> getGraph() {
		return graph;
	}

	public int outDegreeOf(final StateId arg0) {
		return graph.outDegreeOf(arg0);
	}

	public Set<OpInfo> outgoingEdgesOf(final StateId arg0) {
		return graph.outgoingEdgesOf(arg0);
	}

	public boolean addEdge(final StateId arg0, final StateId arg1,
			final OpInfo arg2) {
		return graph.addEdge(arg0, arg1, arg2);
	}

	public boolean addVertex(final StateId arg0) {
		return graph.addVertex(arg0);
	}

	public boolean containsEdge(final OpInfo arg0) {
		return graph.containsEdge(arg0);
	}

	public boolean containsVertex(final StateId arg0) {
		return graph.containsVertex(arg0);
	}

	public Set<OpInfo> edgeSet() {
		return graph.edgeSet();
	}

	public StateId getEdgeTarget(final OpInfo arg0) {
		return graph.getEdgeTarget(arg0);
	}

	public Set<StateId> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		Set<StateId> vertexSet = vertexSet();
		sb.append("(");
		sb.append(vertexSet.toString());
		sb.append(", ");
		Set<OpInfo> edgeSet = edgeSet();

		List<String> list = new ArrayList<String>();
		for (OpInfo opInfo : edgeSet) {
			list.add(opInfo.getId() + "=[" + opInfo.getSrc() + ","
					+ opInfo.getDest() + "]");
		}
		sb.append(list.toString() + ")");
		return sb.toString();
	}

}
