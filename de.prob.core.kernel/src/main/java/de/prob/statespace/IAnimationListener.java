package de.prob.statespace;

/**
 * A class that implements this interface and registers itself with a
 * {@link History} object will receive updates whenever the current state
 * changes.
 * 
 * @author joy
 * 
 */
public interface IAnimationListener {
	/**
	 * Lets the {@link IAnimationListener} know that the {@link History} object
	 * with reference oldHistory has been changed to newHistory so that the
	 * {@link IAnimationListener} can update its registry.
	 * 
	 * @param oldHistory
	 * @param newHistory
	 */
	public void currentStateChanged(History oldHistory, History newHistory);

	/**
	 * Lets the {@link IAnimationListener} know that it should remove the
	 * {@link History} object from its registry.
	 * 
	 * @param history
	 */
	public void removeHistory(History history);
}
