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
	 * @return a String message describing the result that was generated. This
	 *         is subject to change.
	 */
	String getMessage();
}
