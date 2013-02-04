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
	public void historyChange(History history);
}
