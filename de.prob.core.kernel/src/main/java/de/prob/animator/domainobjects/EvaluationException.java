package de.prob.animator.domainobjects;

public class EvaluationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8968134130086367905L;

	public EvaluationException(final String message, final Throwable thrown) {
		super(message, thrown);
	}

	public EvaluationException(final String message) {
		super(message);
	}

}
