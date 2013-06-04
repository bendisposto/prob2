package de.prob.statespace;

/**
 * A Class that implements this interface and registers itself in the
 * {@link AnimationSelector} will receive updates via the
 * {@link IAnimationChangeListener#traceChange(Trace)} method whenever anything
 * happens with the current animation.
 * 
 * @author joy
 * 
 */
public interface IAnimationChangeListener {
	/**
	 * Lets the {@link IAnimationChangeListener} know that the trace of interest
	 * has changed
	 * 
	 * @param trace
	 */
	public void traceChange(Trace trace);
}
