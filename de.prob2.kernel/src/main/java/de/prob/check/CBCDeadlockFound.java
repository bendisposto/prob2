package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.Transition;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

/**
 * Class returned if a deadlock is found during CBC deadlock checking. The
 * counterexample produced by ProB consists of one transition to the error
 * state.
 * 
 * @author joy
 * 
 */
public class CBCDeadlockFound implements IModelCheckingResult,
		ITraceDescription {

	private final String errorId;
	private final Transition transition;

	public CBCDeadlockFound(final String errorId, final Transition transition) {
		this.errorId = errorId;
		this.transition = transition;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		Trace t = new Trace(s);
		t = t.add(transition);
		return t;
	}

	/**
	 * @return String state id associated with the error state found in ProB
	 */
	public String getErrorId() {
		return errorId;
	}

	@Override
	public String getMessage() {
		return "A deadlock was found.";
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
