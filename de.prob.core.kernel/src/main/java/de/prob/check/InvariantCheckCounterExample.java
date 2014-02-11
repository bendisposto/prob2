package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

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
	private final OpInfo step1, step2;

	public InvariantCheckCounterExample(final String eventName,
			final OpInfo step1, final OpInfo step2) {
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
	 * @return the first {@link OpInfo} transition into a state for which the
	 *         invariant holds.
	 */
	public OpInfo getStep1() {
		return step1;
	}

	/**
	 * @return the second {@link OpInfo} transition into a state for which the
	 *         invariant does not hold.
	 */
	public OpInfo getStep2() {
		return step2;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		Trace t = new Trace(s);
		t = t.add(step1.id);
		t = t.add(step2.id);
		return t;
	}
}
