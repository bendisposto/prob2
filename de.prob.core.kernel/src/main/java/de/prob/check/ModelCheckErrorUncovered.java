package de.prob.check;

import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class ModelCheckErrorUncovered implements IModelCheckingResult {

	private final String message;
	private final String errorStateId;
	private final StateSpaceStats stats;

	public ModelCheckErrorUncovered(final StateSpaceStats stats,
			final String message, final String errorStateId) {
		this.stats = stats;
		this.message = message;
		this.errorStateId = errorStateId;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorStateId() {
		return errorStateId;
	}

	public Trace getTraceToErrorState(final StateSpace s) {
		return s.getTrace(s.getVertex(errorStateId));
	}

	@Override
	public StateSpaceStats getStats() {
		return stats;
	}
}
