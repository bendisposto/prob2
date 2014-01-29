package de.prob.check;

import java.util.List;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class CBCInvariantViolationFound implements IModelCheckingResult,
		ITraceDescription {

	private final List<InvariantCheckCounterExample> counterexamples;

	public CBCInvariantViolationFound(
			final List<InvariantCheckCounterExample> counterexamples) {
		this.counterexamples = counterexamples;

	}

	public Trace getTrace(final int index, final StateSpace s) {
		return s.getTrace(counterexamples.get(index));
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		return counterexamples.isEmpty() ? null : s.getTrace(counterexamples
				.get(0));
	}

	@Override
	public String getMessage() {
		return "Invariant violation uncovered.";
	}
}
