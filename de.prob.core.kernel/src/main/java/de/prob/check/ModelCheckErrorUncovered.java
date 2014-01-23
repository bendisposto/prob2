package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class ModelCheckErrorUncovered implements IModelCheckingResult,
		ITraceDescription {

	private final String message;
	private final String errorStateId;
	private final StateSpaceStats stats;
	private final ModelCheckingOptions options;

	public ModelCheckErrorUncovered(final StateSpaceStats stats,
			final String message, final String errorStateId,
			final ModelCheckingOptions options) {
		this.stats = stats;
		this.message = message;
		this.errorStateId = errorStateId;
		this.options = options;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getErrorStateId() {
		return errorStateId;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		return s.getTrace(new StateId(errorStateId, s));
	}

	@Override
	public StateSpaceStats getStats() {
		return stats;
	}

	@Override
	public ModelCheckingOptions getOptions() {
		return options;
	}
}
