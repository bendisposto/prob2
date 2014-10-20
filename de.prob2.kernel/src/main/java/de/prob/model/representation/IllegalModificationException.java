package de.prob.model.representation;

public class IllegalModificationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5460101958681905582L;

	public IllegalModificationException() {
		super(
				"The Model element that you are attempting to modify has been frozen and can therefore no longer be modified.");
	}
}
