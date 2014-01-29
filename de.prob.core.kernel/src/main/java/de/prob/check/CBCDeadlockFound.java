package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class CBCDeadlockFound implements IModelCheckingResult,
		ITraceDescription {

	private final String errorId;
	private final OpInfo transition;

	public CBCDeadlockFound(final String errorId, final OpInfo transition) {
		this.errorId = errorId;
		this.transition = transition;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		Trace t = new Trace(s);
		t = t.add(transition.id);
		return t;
	}

	public String getErrorId() {
		return errorId;
	}

	@Override
	public String getMessage() {
		return "A deadlock was found.";
	}

}
