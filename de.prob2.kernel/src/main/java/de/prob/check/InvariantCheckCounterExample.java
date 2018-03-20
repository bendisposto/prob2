package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * Class created during constraint based invariant checking to represent an
 * invariant violation discovered by ProB. This counterexample consists of two
 * transitions: one into a state for which the invariant holds, and a second
 * into a state for which the invariant does not hold. The
 * {@link #getTrace(StateSpace)} method in this class will replay these two
 * steps to create a trace.
 * 
 * @author joy
 * 
 */
public class InvariantCheckCounterExample implements ITraceDescription {
	private final String eventName;
	private final Transition step1;
	private final Transition step2;

	public InvariantCheckCounterExample(final String eventName,
			final Transition step1, final Transition step2) {
		this.eventName = eventName;
		this.step1 = step1;
		this.step2 = step2;
	}

	/**
	 * @return {@link String} name of the transition which produces the
	 *         invariant violation.
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @return the first {@link Transition} transition into a state for which
	 *         the invariant holds.
	 */
	public Transition getStep1() {
		return step1;
	}

	/**
	 * @return the second {@link Transition} transition into a state for which
	 *         the invariant does not hold.
	 */
	public Transition getStep2() {
		return step2;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		Trace t = new Trace(s);
		t = t.add(step1);
		t = t.add(step2);
		return t;
	}
}
