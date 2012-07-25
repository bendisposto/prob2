package de.prob.statespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

import de.prob.animator.domainobjects.OpInfo;

public class StateSpaceGraph implements Graph<StateId, OpInfo>,
		DirectedGraph<StateId, OpInfo> {

	private final DirectedMultigraph<StateId, OpInfo> graph;

	public StateSpaceGraph(final DirectedMultigraph<StateId, OpInfo> graph) {
		this.graph = graph;
	}

	public DirectedMultigraph<StateId, OpInfo> getGraph() {
		return graph;
	}

	@Override
	public int inDegreeOf(final StateId arg0) {
		return graph.inDegreeOf(arg0);
	}

	@Override
	public Set<OpInfo> incomingEdgesOf(final StateId arg0) {
		return graph.incomingEdgesOf(arg0);
	}

	@Override
	public int outDegreeOf(final StateId arg0) {
		return graph.outDegreeOf(arg0);
	}

	@Override
	public Set<OpInfo> outgoingEdgesOf(final StateId arg0) {
		return graph.outgoingEdgesOf(arg0);
	}

	@Override
	public OpInfo addEdge(final StateId arg0, final StateId arg1) {
		return graph.addEdge(arg0, arg1);
	}

	@Override
	public boolean addEdge(final StateId arg0, final StateId arg1,
			final OpInfo arg2) {
		return graph.addEdge(arg0, arg1, arg2);
	}

	@Override
	public boolean addVertex(final StateId arg0) {
		return graph.addVertex(arg0);
	}

	@Override
	public boolean containsEdge(final OpInfo arg0) {
		return graph.containsEdge(arg0);
	}

	@Override
	public boolean containsEdge(final StateId arg0, final StateId arg1) {
		return graph.containsEdge(arg0, arg1);
	}

	@Override
	public boolean containsVertex(final StateId arg0) {
		return graph.containsVertex(arg0);
	}

	@Override
	public Set<OpInfo> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<OpInfo> edgesOf(final StateId arg0) {
		return graph.edgesOf(arg0);
	}

	@Override
	public Set<OpInfo> getAllEdges(final StateId arg0, final StateId arg1) {
		return graph.getAllEdges(arg0, arg1);
	}

	@Override
	public OpInfo getEdge(final StateId arg0, final StateId arg1) {
		return graph.getEdge(arg0, arg1);
	}

	@Override
	public EdgeFactory<StateId, OpInfo> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public StateId getEdgeSource(final OpInfo arg0) {
		return graph.getEdgeSource(arg0);
	}

	@Override
	public StateId getEdgeTarget(final OpInfo arg0) {
		return graph.getEdgeTarget(arg0);
	}

	@Override
	public double getEdgeWeight(final OpInfo arg0) {
		return graph.getEdgeWeight(arg0);
	}

	@Override
	public boolean removeAllEdges(final Collection<? extends OpInfo> arg0) {
		return graph.removeAllEdges(arg0);
	}

	@Override
	public Set<OpInfo> removeAllEdges(final StateId arg0, final StateId arg1) {
		return graph.removeAllEdges(arg0, arg1);
	}

	@Override
	public boolean removeAllVertices(final Collection<? extends StateId> arg0) {
		return graph.removeAllVertices(arg0);
	}

	@Override
	public boolean removeEdge(final OpInfo arg0) {
		return graph.removeEdge(arg0);
	}

	@Override
	public OpInfo removeEdge(final StateId arg0, final StateId arg1) {
		return graph.removeEdge(arg0, arg1);
	}

	@Override
	public boolean removeVertex(final StateId arg0) {
		return graph.removeVertex(arg0);
	}

	@Override
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
