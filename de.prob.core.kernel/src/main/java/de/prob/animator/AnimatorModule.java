package de.prob.animator;

import com.google.inject.AbstractModule;

import de.prob.model.IStateSpace;

public class AnimatorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAnimator.class).to(AnimatorImpl.class);
		bind(IStateSpace.class).toProvider(StateSpaceProvider.class);
	}
}
