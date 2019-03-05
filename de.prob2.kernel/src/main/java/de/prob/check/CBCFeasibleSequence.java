package de.prob.check;

import java.util.List;

import de.prob.statespace.Transition;

public class CBCFeasibleSequence implements IModelCheckingResult {

	private final List<Transition> ids;
	
	public CBCFeasibleSequence(final List<Transition> ids) {
		this.ids = ids;
	}

	@Override
	public String getMessage() {
		return "A sequence was found";
	}
	
	public List<Transition> getTransitions() {
		return ids;
	}
	
}
