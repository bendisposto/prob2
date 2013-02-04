package de.prob.statespace;

/**
 * A Class that implements this interface and registers itself in the
 * {@link AnimationSelector} will receive updates via the
 * {@link IHistoryChangeListener#historyChange(History)} method whenever
 * anything happens with the current animation.
 * 
 * @author joy
 * 
 */
public interface IHistoryChangeListener {
	/**
	 * Lets the {@link IHistoryChangeListener} know that the history of interest
	 * has changed
	 * 
	 * @param history
	 */
	public void historyChange(History history);
}
