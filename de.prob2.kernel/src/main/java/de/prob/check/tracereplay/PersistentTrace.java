package de.prob.check.tracereplay;

import java.util.ArrayList;
import java.util.List;

import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class PersistentTrace {

	private final List<PersistentTransition> transitionList = new ArrayList<>();

	public PersistentTrace(Trace trace, int count) {
		List<Transition> list = trace.getTransitionList();
		for (int i = 0; i < count; i++) {
			transitionList.add(new PersistentTransition(list.get(i)));
		}
	}
	
	public PersistentTrace(Trace trace) {
		for (Transition transition : trace.getTransitionList()) {
			transitionList.add(new PersistentTransition(transition));
		}
	}

	public List<PersistentTransition> getTransitionList() {
		return this.transitionList;
	}
}