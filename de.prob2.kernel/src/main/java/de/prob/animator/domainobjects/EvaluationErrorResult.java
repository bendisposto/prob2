package de.prob.animator.domainobjects;

import java.util.List;

import com.google.common.base.Joiner;

public abstract class EvaluationErrorResult implements IEvalResult {

	private final String result;
	private final List<String> errors;

	public EvaluationErrorResult(final String result, final List<String> errors) {
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
		return this.result + ": "+Joiner.on(" ").join(errors);
	}

}
