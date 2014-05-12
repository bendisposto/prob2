package de.prob.animator;

import com.google.inject.AbstractModule;

import de.prob.animator.domainobjects.EvalElementFactory;

/**
 * The Guice module responsible for initializing classes having to do with the
 * interaction with the prolog core.
 * 
 * @author joy
 * 
 */
public class AnimatorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAnimator.class).to(AnimatorImpl.class);
		bind(CommandProcessor.class);
		bind(EvalElementFactory.class);

	}
}
