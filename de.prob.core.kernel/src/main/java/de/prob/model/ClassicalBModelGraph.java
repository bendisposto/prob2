package de.prob.model;

import java.util.Collection;

import de.prob.model.representation.ClassicalBMachine;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ClassicalBModelGraph
	implements MultiGraph<ClassicalBMachine, ClassicalBDependencyType>,
	DirectedGraph<ClassicalBMachine, ClassicalBDependencyType> {

	private final DirectedSparseMultigraph<ClassicalBMachine,
		ClassicalBDependencyType> graph;
	
	public ClassicalBModelGraph(final DirectedSparseMultigraph<ClassicalBMachine, ClassicalBDependencyType> graph) {
		this.graph = graph;
	}
	
		
	public boolean addEdge(ClassicalBDependencyType edge,
			Collection<? extends ClassicalBMachine> vertices) {
		return graph.addEdge(edge, vertices);
	}

	public boolean addEdge(ClassicalBDependencyType edge,
			Collection<? extends ClassicalBMachine> vertices, EdgeType edgeType) {
		return graph.addEdge(edge, vertices, edgeType);
	}

	public EdgeType getDefaultEdgeType() {
		return graph.getDefaultEdgeType();
	}

	public EdgeType getEdgeType(ClassicalBDependencyType e) {
		return graph.getEdgeType(e);
	}

	public Collection<ClassicalBDependencyType> getEdges(EdgeType edge_type) {
		return graph.getEdges(edge_type);
	}

	public int getEdgeCount(EdgeType edge_type) {
		return graph.getEdgeCount(edge_type);
	}

	public boolean addEdge(ClassicalBDependencyType e, ClassicalBMachine v1,
			ClassicalBMachine v2) {
		return graph.addEdge(e, v1, v2);
	}

	public Collection<ClassicalBDependencyType> getEdges() {
		return graph.getEdges();
	}

	public boolean addEdge(ClassicalBDependencyType e, ClassicalBMachine v1,
			ClassicalBMachine v2, EdgeType edge_type) {
		return graph.addEdge(e, v1, v2, edge_type);
	}

	public Collection<ClassicalBMachine> getVertices() {
		return graph.getVertices();
	}

	public boolean addEdge(ClassicalBDependencyType edge,
			Pair<? extends ClassicalBMachine> endpoints) {
		return graph.addEdge(edge, endpoints);
	}

	public boolean containsVertex(ClassicalBMachine vertex) {
		return graph.containsVertex(vertex);
	}

	public boolean containsEdge(ClassicalBDependencyType edge) {
		return graph.containsEdge(edge);
	}

	public boolean addVertex(ClassicalBMachine vertex) {
		return graph.addVertex(vertex);
	}

	public Collection<ClassicalBDependencyType> getInEdges(
			ClassicalBMachine vertex) {
		return graph.getInEdges(vertex);
	}

	public int getPredecessorCount(ClassicalBMachine vertex) {
		return graph.getPredecessorCount(vertex);
	}

	public Collection<ClassicalBDependencyType> getOutEdges(
			ClassicalBMachine vertex) {
		return graph.getOutEdges(vertex);
	}

	public int getSuccessorCount(ClassicalBMachine vertex) {
		return graph.getSuccessorCount(vertex);
	}

	public Collection<ClassicalBMachine> getPredecessors(
			ClassicalBMachine vertex) {
		return graph.getPredecessors(vertex);
	}

	public Collection<ClassicalBMachine> getSuccessors(ClassicalBMachine vertex) {
		return graph.getSuccessors(vertex);
	}

	public int getNeighborCount(ClassicalBMachine vertex) {
		return graph.getNeighborCount(vertex);
	}

	public Collection<ClassicalBMachine> getNeighbors(ClassicalBMachine vertex) {
		return graph.getNeighbors(vertex);
	}

	public int degree(ClassicalBMachine vertex) {
		return graph.degree(vertex);
	}

	public int getIncidentCount(ClassicalBDependencyType edge) {
		return graph.getIncidentCount(edge);
	}

	public Collection<ClassicalBDependencyType> getIncidentEdges(
			ClassicalBMachine vertex) {
		return graph.getIncidentEdges(vertex);
	}

	public ClassicalBMachine getOpposite(ClassicalBMachine vertex,
			ClassicalBDependencyType edge) {
		return graph.getOpposite(vertex, edge);
	}

	public ClassicalBDependencyType findEdge(ClassicalBMachine v1,
			ClassicalBMachine v2) {
		return graph.findEdge(v1, v2);
	}

	public boolean addEdge(ClassicalBDependencyType edge,
			Pair<? extends ClassicalBMachine> endpoints, EdgeType edgeType) {
		return graph.addEdge(edge, endpoints, edgeType);
	}

	public Collection<ClassicalBDependencyType> findEdgeSet(
			ClassicalBMachine v1, ClassicalBMachine v2) {
		return graph.findEdgeSet(v1, v2);
	}

	public ClassicalBMachine getSource(ClassicalBDependencyType edge) {
		return graph.getSource(edge);
	}

	public Collection<ClassicalBMachine> getIncidentVertices(
			ClassicalBDependencyType edge) {
		return graph.getIncidentVertices(edge);
	}

	public ClassicalBMachine getDest(ClassicalBDependencyType edge) {
		return graph.getDest(edge);
	}

	public Pair<ClassicalBMachine> getEndpoints(ClassicalBDependencyType edge) {
		return graph.getEndpoints(edge);
	}

	public int getEdgeCount() {
		return graph.getEdgeCount();
	}

	public int getVertexCount() {
		return graph.getVertexCount();
	}

	public int hashCode() {
		return graph.hashCode();
	}

	public boolean removeVertex(ClassicalBMachine vertex) {
		return graph.removeVertex(vertex);
	}

	public boolean removeEdge(ClassicalBDependencyType edge) {
		return graph.removeEdge(edge);
	}

	public int inDegree(ClassicalBMachine vertex) {
		return graph.inDegree(vertex);
	}

	public int outDegree(ClassicalBMachine vertex) {
		return graph.outDegree(vertex);
	}

	public boolean isPredecessor(ClassicalBMachine v1, ClassicalBMachine v2) {
		return graph.isPredecessor(v1, v2);
	}

	public boolean isSuccessor(ClassicalBMachine v1, ClassicalBMachine v2) {
		return graph.isSuccessor(v1, v2);
	}

	public boolean isNeighbor(ClassicalBMachine v1, ClassicalBMachine v2) {
		return graph.isNeighbor(v1, v2);
	}

	public boolean isIncident(ClassicalBMachine vertex,
			ClassicalBDependencyType edge) {
		return graph.isIncident(vertex, edge);
	}

	public boolean isSource(ClassicalBMachine vertex,
			ClassicalBDependencyType edge) {
		return graph.isSource(vertex, edge);
	}

	public String toString() {
		return graph.toString();
	}

	public boolean isDest(ClassicalBMachine vertex,
			ClassicalBDependencyType edge) {
		return graph.isDest(vertex, edge);
	}
}
