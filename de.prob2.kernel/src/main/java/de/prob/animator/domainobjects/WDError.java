package de.prob.animator.domainobjects;

import java.util.List;

public class WDError extends EvaluationErrorResult {
	public static final String MESSAGE = "NOT-WELL-DEFINED";

	public WDError(final List<String> errors) {
		super(MESSAGE,errors);
	}
}
