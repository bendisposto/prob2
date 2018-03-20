package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class RefinementCheckCounterExample implements ITraceDescription {
	private final String eventName;
	private final Transition step1;
	private final Transition step2;

	public RefinementCheckCounterExample(final String eventName,
			final Transition step1, final Transition step2) {
		this.eventName = eventName;
		this.step1 = step1;
		this.step2 = step2;
	}

	public String getEventName() {
		return eventName;
	}

	public Transition getStep1() {
		return step1;
	}

	public Transition getStep2() {
		return step2;
	}

	@Override
	public Trace getTrace(StateSpace s) {
		Trace t = new Trace(s);
		t = t.add(step1);
		t = t.add(step2);
		return t;
	}
}
