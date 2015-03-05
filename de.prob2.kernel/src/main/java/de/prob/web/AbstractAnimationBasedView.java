package de.prob.web;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;

public abstract class AbstractAnimationBasedView extends AbstractSession
		implements IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(AbstractAnimationBasedView.class);
	protected final AnimationSelector animationsRegistry;
	boolean multianimation;
	protected UUID animationOfInterest;

	@Inject
	public AbstractAnimationBasedView(final AnimationSelector animations) {
		this.animationsRegistry = animations;
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		if (animationOfInterest != null) {
			performTraceChange(animationsRegistry.getTrace(animationOfInterest));
		} else if (animationOfInterest == null && currentAnimationChanged) {
			performTraceChange(currentTrace);
		}
	}

	public abstract void performTraceChange(Trace trace);

	public Trace getCurrentTrace() {
		return animationOfInterest == null ? animationsRegistry
				.getCurrentTrace() : animationsRegistry
				.getTrace(animationOfInterest);
	}

	public void setAnimationOfInterest(final UUID animationOfInterest) {
		this.animationOfInterest = animationOfInterest;
		performTraceChange(animationsRegistry.getTrace(animationOfInterest));
	}

	public UUID getAnimationOfInterest() {
		return animationOfInterest;
	}
}
