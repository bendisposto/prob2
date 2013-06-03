package de.prob.statespace;

/**
 * A Class that implements this interface and registers itself in the
 * {@link AnimationSelector} will receive updates via the
 * {@link IAnimationChangedListener#historyChange(Trace)} method whenever
 * anything happens with the current animation.
 * 
 * @author joy
 * 
 */
public interface IAnimationChangedListener {
	/**
	 * Lets the {@link IAnimationChangedListener} know that the history of interest
	 * has changed
	 * 
	 * @param history
	 */
	public void historyChange(Trace history);
}
