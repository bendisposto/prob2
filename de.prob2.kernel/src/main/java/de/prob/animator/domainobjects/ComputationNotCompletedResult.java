package de.prob.animator.domainobjects;

public class ComputationNotCompletedResult extends AbstractEvalResult {

	private final String reason;
	private final String code;

	public ComputationNotCompletedResult(final String code, final String reason) {
		super();
		this.code = code;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return reason;
	}

}
