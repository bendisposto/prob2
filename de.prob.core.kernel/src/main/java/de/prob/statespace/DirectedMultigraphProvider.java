package de.prob.statespace;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DirectedMultigraphProvider implements
		Provider<DirectedMultigraph<StateId, OperationId>> {

	@Override
	public DirectedMultigraph<StateId, OperationId> get() {
		return new DirectedMultigraph<StateId, OperationId>(
				new ClassBasedEdgeFactory<StateId, OperationId>(
						OperationId.class));
	}
}
