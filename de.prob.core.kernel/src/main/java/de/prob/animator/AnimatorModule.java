package de.prob.animator;

import com.google.inject.AbstractModule;

public class AnimatorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAnimator.class).to(AnimatorImpl.class);
	}
}
