package de.prob.statespace;

/**
 * A Class that implements this interface and registers itself in the
 * {@link AnimationSelector} will receive updates via the
 * {@link IAnimationChangeListener#traceChange(Trace, boolean)} method whenever anything
 * happens with the current animation.
 * 
 * @author joy
 * 
 */
public interface IAnimationChangeListener {
	/**
	 * Lets the {@link IAnimationChangeListener} know that a trace change has
	 * taken place. The current trace is given as a parameter, and a flag
	 * specifies if the change that has taken place in the animation selector
	 * changed the current trace or not. If a listener only cares for changes
	 * that take place in the current animation, they should only react when
	 * flag currentAnimationChanged is set to true. If currentAnimationChanged
	 * is set to false, this means that an animation change has taken place in
	 * one of the other traces that is not currently being considered. In this
	 * case, the listener can inspect the animation selector object to view the
	 * total current state of the object.
	 * 
	 * @param currentTrace
	 *            the current trace in the animations object
	 * @param currentAnimationChanged
	 *            whether or not the current animation has changed.
	 */
	public void traceChange(Trace currentTrace, boolean currentAnimationChanged);

	public void animatorStatus(boolean busy);
}
