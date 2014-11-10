package de.prob.statespace;

import com.google.inject.Provider;
import com.google.inject.Singleton;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

@Singleton
public class DirectedMultigraphProvider implements
		Provider<DirectedSparseMultigraph<State, Transition>> {

	@Override
	public DirectedSparseMultigraph<State, Transition> get() {
		return new DirectedSparseMultigraph<State, Transition>();
	}
}
