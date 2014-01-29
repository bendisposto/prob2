package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class InvariantCheckCounterExample implements ITraceDescription {
	private final String eventName;
	private final OpInfo step1, step2;

	public InvariantCheckCounterExample(final String eventName,
			final OpInfo step1, final OpInfo step2) {
		this.eventName = eventName;
		this.step1 = step1;
		this.step2 = step2;
	}

	public String getEventName() {
		return eventName;
	}

	public OpInfo getStep1() {
		return step1;
	}

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
