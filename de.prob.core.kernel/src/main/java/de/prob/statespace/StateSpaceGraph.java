package de.prob.statespace;

import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class StateSpaceGraph implements MultiGraph<StateId, OperationId>,
		DirectedGraph<StateId, OperationId> {

	private final DirectedSparseMultigraph<StateId, OperationId> graph;

	public StateSpaceGraph(
			final DirectedSparseMultigraph<StateId, OperationId> graph) {
		this.graph = graph;
	}

	@Override
	public boolean addEdge(final OperationId edge,
			final Collection<? extends StateId> vertices) {
		return graph.addEdge(edge, vertices);
	}

	@Override
	public boolean addEdge(final OperationId edge,
			final Collection<? extends StateId> vertices,
			final EdgeType edgeType) {
		return graph.addEdge(edge, vertices, edgeType);
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		return graph.getDefaultEdgeType();
	}

	@Override
	public EdgeType getEdgeType(final OperationId e) {
		return graph.getEdgeType(e);
	}

	@Override
	public Collection<OperationId> getEdges(final EdgeType edge_type) {
		return graph.getEdges(edge_type);
	}

	@Override
	public int getEdgeCount(final EdgeType edge_type) {
		return graph.getEdgeCount(edge_type);
	}

	@Override
	public boolean addEdge(final OperationId e, final StateId v1,
			final StateId v2) {
		return graph.addEdge(e, v1, v2, EdgeType.DIRECTED);
	}

	@Override
	public Collection<OperationId> getEdges() {
		return graph.getEdges();
	}

	@Override
	public boolean addEdge(final OperationId e, final StateId v1,
			final StateId v2, final EdgeType edge_type) {
		return graph.addEdge(e, v1, v2, edge_type);
	}

	@Override
	public Collection<StateId> getVertices() {
		return graph.getVertices();
	}

	public boolean addEdge(final OperationId edge,
			final Pair<? extends StateId> endpoints) {
		return graph.addEdge(edge, endpoints);
	}

	@Override
	public boolean containsVertex(final StateId vertex) {
		return graph.containsVertex(vertex);
	}

	public boolean containsVertex(final String vertex) {
		return containsVertex(new StateId(vertex));
	}

	@Override
	public boolean containsEdge(final OperationId edge) {
		return graph.containsEdge(edge);
	}

	public boolean containsEdge(final String edge) {
		return containsEdge(new OperationId(edge));
	}

	@Override
	public boolean addVertex(final StateId vertex) {
		return graph.addVertex(vertex);
	}

	@Override
	public Collection<OperationId> getInEdges(final StateId vertex) {
		return graph.getInEdges(vertex);
	}

	@Override
	public int getPredecessorCount(final StateId vertex) {
		return graph.getPredecessorCount(vertex);
	}

	@Override
	public Collection<OperationId> getOutEdges(final StateId vertex) {
		return graph.getOutEdges(vertex);
	}

	public Collection<OperationId> getOutEdges(final String vertex) {
		return graph.getOutEdges(new StateId(vertex));
	}

	@Override
	public int getSuccessorCount(final StateId vertex) {
		return graph.getSuccessorCount(vertex);
	}

	@Override
	public Collection<StateId> getPredecessors(final StateId vertex) {
		return graph.getPredecessors(vertex);
	}

	@Override
	public Collection<StateId> getSuccessors(final StateId vertex) {
		return graph.getSuccessors(vertex);
	}

	@Override
	public int getNeighborCount(final StateId vertex) {
		return graph.getNeighborCount(vertex);
	}

	@Override
	public Collection<StateId> getNeighbors(final StateId vertex) {
		return graph.getNeighbors(vertex);
	}

	@Override
	public int degree(final StateId vertex) {
		return graph.degree(vertex);
	}

	@Override
	public int getIncidentCount(final OperationId edge) {
		return graph.getIncidentCount(edge);
	}

	@Override
	public Collection<OperationId> getIncidentEdges(final StateId vertex) {
		return graph.getIncidentEdges(vertex);
	}

	@Override
	public StateId getOpposite(final StateId vertex, final OperationId edge) {
		return graph.getOpposite(vertex, edge);
	}

	public boolean addEdge(final OperationId edge,
			final Pair<? extends StateId> endpoints, final EdgeType edgeType) {
		return graph.addEdge(edge, endpoints, edgeType);
	}

	@Override
	public OperationId findEdge(final StateId v1, final StateId v2) {
		return graph.findEdge(v1, v2);
	}

	@Override
	public Collection<OperationId> findEdgeSet(final StateId v1,
			final StateId v2) {
		return graph.findEdgeSet(v1, v2);
	}

	@Override
	public StateId getSource(final OperationId edge) {
		return graph.getSource(edge);
	}

	public StateId getSource(final String edge) {
		return graph.getSource(new OperationId(edge));
	}

	@Override
	public Collection<StateId> getIncidentVertices(final OperationId edge) {
		return graph.getIncidentVertices(edge);
	}

	@Override
	public StateId getDest(final OperationId edge) {
		return graph.getDest(edge);
	}

	public StateId getDest(final String edge) {
		return graph.getDest(new OperationId(edge));
	}

	@Override
	public Pair<StateId> getEndpoints(final OperationId edge) {
		return graph.getEndpoints(edge);
	}

	@Override
	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	@Override
	public int getVertexCount() {
		return graph.getVertexCount();
	}

	@Override
	public int hashCode() {
		return graph.hashCode();
	}

	@Override
	public boolean removeVertex(final StateId vertex) {
		return graph.removeVertex(vertex);
	}

	@Override
	public boolean removeEdge(final OperationId edge) {
		return graph.removeEdge(edge);
	}

	@Override
	public int inDegree(final StateId vertex) {
		return graph.inDegree(vertex);
	}

	@Override
	public int outDegree(final StateId vertex) {
		return graph.outDegree(vertex);
	}

	@Override
	public boolean isPredecessor(final StateId v1, final StateId v2) {
		return graph.isPredecessor(v1, v2);
	}

	@Override
	public boolean isSuccessor(final StateId v1, final StateId v2) {
		return graph.isSuccessor(v1, v2);
	}

	@Override
	public boolean isNeighbor(final StateId v1, final StateId v2) {
		return graph.isNeighbor(v1, v2);
	}

	@Override
	public boolean isIncident(final StateId vertex, final OperationId edge) {
		return graph.isIncident(vertex, edge);
	}

	@Override
	public boolean isSource(final StateId vertex, final OperationId edge) {
		return graph.isSource(vertex, edge);
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public boolean isDest(final StateId vertex, final OperationId edge) {
		return graph.isDest(vertex, edge);
	}

}
