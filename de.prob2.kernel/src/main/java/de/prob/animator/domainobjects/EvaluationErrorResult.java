package de.prob.animator.domainobjects;

import java.util.List;

public abstract class EvaluationErrorResult extends AbstractEvalResult {

	private final String result;
	private final List<String> errors;

	public EvaluationErrorResult(final String result, final List<String> errors) {
		super();
		this.result = result;
		this.errors = errors;
	}

	public String getResult() {
		return result;
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return this.result + ": " + String.join(" ", errors);
	}

}
