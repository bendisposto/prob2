package de.prob.animator.domainobjects;

import java.util.List;

public class IdentifierNotInitialised extends EvaluationErrorResult {

	public IdentifierNotInitialised(final List<String> errors) {
		super("NOT-INITIALISED", errors);
	}

}
