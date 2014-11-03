package de.prob.check;

import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class ModelCheckErrorUncovered implements IModelCheckingResult,
		ITraceDescription {

	private final String message;
	private final String errorStateId;

	public ModelCheckErrorUncovered(final String message,
			final String errorStateId) {
		this.message = message;
		this.errorStateId = errorStateId;
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
		return s.getTrace(errorStateId);
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
