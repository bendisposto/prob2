package de.prob.web;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.Main;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;

public abstract class AbstractAnimationBasedView extends AbstractSession
		implements IAnimationChangeListener {

	Logger logger = LoggerFactory.getLogger(AbstractAnimationBasedView.class);
	protected final AnimationSelector animationsRegistry;
	boolean multianimation;
	private final UUID animationOfInterest;

	@Inject
	public AbstractAnimationBasedView(final AnimationSelector animations,
			final UUID animationOfInterest) {
		this.animationsRegistry = animations;
		if (Main.multianimation) {
			this.animationOfInterest = animationOfInterest;
		} else {
			this.animationOfInterest = null;
		}
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		if (animationOfInterest != null) {
			if (currentTrace != null
					&& currentTrace.getUUID().equals(animationOfInterest)) {
				performTraceChange(currentTrace);
			}
		} else if (animationOfInterest == null && currentAnimationChanged) {
			performTraceChange(currentTrace);
		}
	}

	public abstract void performTraceChange(Trace trace);
}
