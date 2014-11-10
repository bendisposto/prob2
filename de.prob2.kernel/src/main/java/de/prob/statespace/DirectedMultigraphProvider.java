package de.prob.statespace;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

@Singleton
public class DirectedMultigraphProvider implements
		Provider<DirectedSparseMultigraph<State, OpInfo>> {

	@Override
	public DirectedSparseMultigraph<State, OpInfo> get() {
		return new DirectedSparseMultigraph<State, OpInfo>();
	}
}
