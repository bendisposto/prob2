package de.prob.statespace;

import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class StateSpaceGraph implements MultiGraph<String, String>,
		DirectedGraph<String, String> {

	private final DirectedSparseMultigraph<String, String> graph;

	public StateSpaceGraph(final DirectedSparseMultigraph<String, String> graph) {
		this.graph = graph;
	}

	@Override
	public boolean addEdge(final String edge,
			final Collection<? extends String> vertices) {
		return graph.addEdge(edge, vertices);
	}

	@Override
	public boolean addEdge(final String edge,
			final Collection<? extends String> vertices, final EdgeType edgeType) {
		return graph.addEdge(edge, vertices, edgeType);
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		return graph.getDefaultEdgeType();
	}

	@Override
	public EdgeType getEdgeType(final String e) {
		return graph.getEdgeType(e);
	}

	@Override
	public Collection<String> getEdges(final EdgeType edge_type) {
		return graph.getEdges(edge_type);
	}

	@Override
	public int getEdgeCount(final EdgeType edge_type) {
		return graph.getEdgeCount(edge_type);
	}

	@Override
	public boolean addEdge(final String e, final String v1, final String v2) {
		return graph.addEdge(e, v1, v2);
	}

	@Override
	public Collection<String> getEdges() {
		return graph.getEdges();
	}

	@Override
	public boolean addEdge(final String e, final String v1, final String v2,
			final EdgeType edge_type) {
		return graph.addEdge(e, v1, v2, edge_type);
	}

	@Override
	public Collection<String> getVertices() {
		return graph.getVertices();
	}

	public boolean addEdge(final String edge,
			final Pair<? extends String> endpoints) {
		return graph.addEdge(edge, endpoints);
	}

	@Override
	public boolean containsVertex(final String vertex) {
		return graph.containsVertex(vertex);
	}

	@Override
	public boolean containsEdge(final String edge) {
		return graph.containsEdge(edge);
	}

	@Override
	public boolean addVertex(final String vertex) {
		return graph.addVertex(vertex);
	}

	@Override
	public Collection<String> getInEdges(final String vertex) {
		return graph.getInEdges(vertex);
	}

	@Override
	public int getPredecessorCount(final String vertex) {
		return graph.getPredecessorCount(vertex);
	}

	@Override
	public Collection<String> getOutEdges(final String vertex) {
		return graph.getOutEdges(vertex);
	}

	@Override
	public int getSuccessorCount(final String vertex) {
		return graph.getSuccessorCount(vertex);
	}

	@Override
	public Collection<String> getPredecessors(final String vertex) {
		return graph.getPredecessors(vertex);
	}

	@Override
	public Collection<String> getSuccessors(final String vertex) {
		return graph.getSuccessors(vertex);
	}

	@Override
	public int getNeighborCount(final String vertex) {
		return graph.getNeighborCount(vertex);
	}

	@Override
	public Collection<String> getNeighbors(final String vertex) {
		return graph.getNeighbors(vertex);
	}

	@Override
	public int degree(final String vertex) {
		return graph.degree(vertex);
	}

	@Override
	public int getIncidentCount(final String edge) {
		return graph.getIncidentCount(edge);
	}

	@Override
	public Collection<String> getIncidentEdges(final String vertex) {
		return graph.getIncidentEdges(vertex);
	}

	@Override
	public String getOpposite(final String vertex, final String edge) {
		return graph.getOpposite(vertex, edge);
	}

	public boolean addEdge(final String edge,
			final Pair<? extends String> endpoints, final EdgeType edgeType) {
		return graph.addEdge(edge, endpoints, edgeType);
	}

	@Override
	public String findEdge(final String v1, final String v2) {
		return graph.findEdge(v1, v2);
	}

	@Override
	public Collection<String> findEdgeSet(final String v1, final String v2) {
		return graph.findEdgeSet(v1, v2);
	}

	@Override
	public String getSource(final String edge) {
		return graph.getSource(edge);
	}

	@Override
	public Collection<String> getIncidentVertices(final String edge) {
		return graph.getIncidentVertices(edge);
	}

	@Override
	public String getDest(final String edge) {
		return graph.getDest(edge);
	}

	@Override
	public Pair<String> getEndpoints(final String edge) {
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
	public boolean removeVertex(final String vertex) {
		return graph.removeVertex(vertex);
	}

	@Override
	public boolean removeEdge(final String edge) {
		return graph.removeEdge(edge);
	}

	@Override
	public int inDegree(final String vertex) {
		return graph.inDegree(vertex);
	}

	@Override
	public int outDegree(final String vertex) {
		return graph.outDegree(vertex);
	}

	@Override
	public boolean isPredecessor(final String v1, final String v2) {
		return graph.isPredecessor(v1, v2);
	}

	@Override
	public boolean isSuccessor(final String v1, final String v2) {
		return graph.isSuccessor(v1, v2);
	}

	@Override
	public boolean isNeighbor(final String v1, final String v2) {
		return graph.isNeighbor(v1, v2);
	}

	@Override
	public boolean isIncident(final String vertex, final String edge) {
		return graph.isIncident(vertex, edge);
	}

	@Override
	public boolean isSource(final String vertex, final String edge) {
		return graph.isSource(vertex, edge);
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public boolean isDest(final String vertex, final String edge) {
		return graph.isDest(vertex, edge);
	}

}
