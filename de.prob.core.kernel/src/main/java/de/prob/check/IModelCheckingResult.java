package de.prob.check;

/**
 * Classes used to represent the result generated from the ModelChecker should
 * implement this interface
 * 
 * @author joy
 * 
 */
public interface IModelCheckingResult {
	/**
	 * @return the {@link StateSpaceStats} for the state space corresponding to
	 *         when this result was generated
	 */
	StateSpaceStats getStats();

	/**
	 * @return the {@link ModelCheckingOptions} object used when generating this
	 *         result.
	 */
	ModelCheckingOptions getOptions();

	/**
	 * @return a String message describing the result that was generated. This
	 *         is subject to change.
	 */
	String getMessage();
}
