package de.prob.statespace;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

public class StateSpaceGraph implements Graph<StateId, OperationId>,
		DirectedGraph<StateId, OperationId> {

	private final DirectedMultigraph<StateId, OperationId> graph;

	public StateSpaceGraph(final DirectedMultigraph<StateId, OperationId> graph) {
		this.graph = graph;
	}

	public DirectedMultigraph<StateId, OperationId> getGraph() {
		return graph;
	}

	@Override
	public int inDegreeOf(final StateId arg0) {
		return graph.inDegreeOf(arg0);
	}

	@Override
	public Set<OperationId> incomingEdgesOf(final StateId arg0) {
		return graph.incomingEdgesOf(arg0);
	}

	@Override
	public int outDegreeOf(final StateId arg0) {
		return graph.outDegreeOf(arg0);
	}

	@Override
	public Set<OperationId> outgoingEdgesOf(final StateId arg0) {
		return graph.outgoingEdgesOf(arg0);
	}

	@Override
	public OperationId addEdge(final StateId arg0, final StateId arg1) {
		return graph.addEdge(arg0, arg1);
	}

	@Override
	public boolean addEdge(final StateId arg0, final StateId arg1,
			final OperationId arg2) {
		return graph.addEdge(arg0, arg1, arg2);
	}

	@Override
	public boolean addVertex(final StateId arg0) {
		return graph.addVertex(arg0);
	}

	@Override
	public boolean containsEdge(final OperationId arg0) {
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
	public Set<OperationId> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<OperationId> edgesOf(final StateId arg0) {
		return graph.edgesOf(arg0);
	}

	@Override
	public Set<OperationId> getAllEdges(final StateId arg0, final StateId arg1) {
		return graph.getAllEdges(arg0, arg1);
	}

	@Override
	public OperationId getEdge(final StateId arg0, final StateId arg1) {
		return graph.getEdge(arg0, arg1);
	}

	@Override
	public EdgeFactory<StateId, OperationId> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	public StateId getEdgeSource(final OperationId arg0) {
		return graph.getEdgeSource(arg0);
	}

	@Override
	public StateId getEdgeTarget(final OperationId arg0) {
		return graph.getEdgeTarget(arg0);
	}

	@Override
	public double getEdgeWeight(final OperationId arg0) {
		return graph.getEdgeWeight(arg0);
	}

	@Override
	public boolean removeAllEdges(final Collection<? extends OperationId> arg0) {
		return graph.removeAllEdges(arg0);
	}

	@Override
	public Set<OperationId> removeAllEdges(final StateId arg0,
			final StateId arg1) {
		return graph.removeAllEdges(arg0, arg1);
	}

	@Override
	public boolean removeAllVertices(final Collection<? extends StateId> arg0) {
		return graph.removeAllVertices(arg0);
	}

	@Override
	public boolean removeEdge(final OperationId arg0) {
		return graph.removeEdge(arg0);
	}

	@Override
	public OperationId removeEdge(final StateId arg0, final StateId arg1) {
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

	public StateId getVertex(final String stateId) {
		final Set<StateId> set = vertexSet();
		for (StateId element : set) {
			if (element.getId().equals(stateId))
				return element;
		}
		return null;
	}

	@Override
	public String toString() {
		return graph.toString();
	}
}
