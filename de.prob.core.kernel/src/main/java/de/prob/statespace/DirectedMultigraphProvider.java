package de.prob.statespace;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.OpInfo;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

@Singleton
public class DirectedMultigraphProvider implements
		Provider<DirectedSparseMultigraph<StateId, OpInfo>> {

	@Override
	public DirectedSparseMultigraph<StateId, OpInfo> get() {
		return new DirectedSparseMultigraph<StateId, OpInfo>();
	}
}
