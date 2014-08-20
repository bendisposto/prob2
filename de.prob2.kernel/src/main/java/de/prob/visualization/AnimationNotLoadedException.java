package de.prob.visualization;

import de.prob.statespace.AnimationSelector;

/**
 * @author joy This {@link Exception} is thrown when a visualization is opened
 *         but there is no animation loaded in the {@link AnimationSelector}.
 */
public class AnimationNotLoadedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2066408268095355170L;

	public AnimationNotLoadedException(final String message) {
		super(message);
	}

}
