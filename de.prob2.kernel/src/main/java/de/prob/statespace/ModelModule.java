package de.prob.statespace;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import de.prob.Main;
import de.prob.annotations.MaxCacheSize;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.CSPModel;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(StateSpace.class);
		bind(ClassicalBModel.class);
		bind(EventBModel.class);
		bind(CSPModel.class);
		bind(AnimationSelector.class);
	}

	@Provides
	@MaxCacheSize
	public final int getMaxSizeForStateCache() {
		return Main.getMaxCacheSize();
	}
}
