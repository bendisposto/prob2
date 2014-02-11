package de.prob.check;

import java.util.List;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

/**
 * Class returned if one or more counterexamples are discovered during
 * constraint based invariant checking. The default
 * {@link #getTrace(StateSpace)} method native to all {@link ITraceDescription}
 * returns the {@link Trace} that is replayed from the first
 * {@link InvariantCheckCounterExample} that was found.
 * 
 * @author joy
 * 
 */
public class CBCInvariantViolationFound implements IModelCheckingResult,
		ITraceDescription {

	private final List<InvariantCheckCounterExample> counterexamples;

	public CBCInvariantViolationFound(
			final List<InvariantCheckCounterExample> counterexamples) {
		this.counterexamples = counterexamples;

	}

	/**
	 * @return {@link List} of {@link InvariantCheckCounterExample}s produced
	 *         during CBC invariant checking
	 */
	public List<InvariantCheckCounterExample> getCounterexamples() {
		return counterexamples;
	}

	/**
	 * @param index
	 *            int value of the index in the list of counterexamples
	 * @param s
	 *            {@link StateSpace} through which the specified counterexample
	 *            should be replayed
	 * @return {@link Trace} created after replaying the counterexample. This
	 *         calls {@link StateSpace#getTrace(ITraceDescription)}.
	 */
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
