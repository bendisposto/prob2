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
	private UUID animation;

	@Inject
	public AbstractAnimationBasedView(final AnimationSelector animations) {
		this.animationsRegistry = animations;
		if (Main.multianimation) {
			Trace current = animationsRegistry.getCurrentTrace();
			if (current == null) {
				animation = null;
				logger.error("Binding a UI component to an animation failed because"
						+ " no animation is loaded");
			} else {
				animation = current.getUUID();
			}
		} else {
			animation = null;
		}
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		if ((animation == null && currentAnimationChanged)
				|| (currentTrace != null && currentTrace.getUUID().equals(
						animation))) {
			performTraceChange(currentTrace);
		}
	}

	public abstract void performTraceChange(Trace trace);
}
