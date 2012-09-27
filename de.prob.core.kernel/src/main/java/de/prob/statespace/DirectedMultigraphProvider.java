package de.prob.statespace;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.OpInfo;

@Singleton
public class DirectedMultigraphProvider implements
		Provider<DirectedMultigraph<StateId, OpInfo>> {

	@Override
	public DirectedMultigraph<StateId, OpInfo> get() {
		return new DirectedMultigraph<StateId, OpInfo>(
				new ClassBasedEdgeFactory<StateId, OpInfo>(OpInfo.class));
	}
}
