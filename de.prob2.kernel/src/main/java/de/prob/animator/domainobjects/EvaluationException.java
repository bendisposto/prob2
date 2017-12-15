package de.prob.animator.domainobjects;

public class EvaluationException extends RuntimeException {
	private static final long serialVersionUID = 8968134130086367905L;

	public EvaluationException() {
		super();
	}

	public EvaluationException(final String message) {
		super(message);
	}

	public EvaluationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EvaluationException(final Throwable cause) {
		super(cause);
	}
}
