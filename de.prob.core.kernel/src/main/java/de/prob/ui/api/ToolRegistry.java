package de.prob.ui.api;

import com.google.inject.Inject;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;

public class ToolRegistry {

	private AnimationSelector animations;

	@Inject
	public ToolRegistry(AnimationSelector animations) {
		this.animations = animations;
	}

	public void stateChange(Trace id, String oldref, String newRef) {
animations.replaceTrace(oldTrace, newTrace)
	}
}
