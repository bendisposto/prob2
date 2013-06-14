package de.prob.animator;

import com.google.inject.AbstractModule;

import de.prob.animator.domainobjects.EvalElementFactory;

public class AnimatorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAnimator.class).to(AnimatorImpl.class);
		bind(CommandProcessor.class);
		bind(EvalElementFactory.class);
	}
}
