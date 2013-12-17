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
}
