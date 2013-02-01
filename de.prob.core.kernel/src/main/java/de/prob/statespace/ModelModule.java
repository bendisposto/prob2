package de.prob.statespace;

import com.google.inject.AbstractModule;

import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.CSPModel;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		// install(new FactoryModuleBuilder()
		// .implement(Payment.class, RealPayment.class)
		// .build(PaymentFactory.class));

		bind(StateSpace.class);
		bind(DirectedMultigraphProvider.class);
		bind(ClassicalBModel.class);
		bind(EventBModel.class);
		bind(CSPModel.class);
		bind(AnimationSelector.class);
	}
}
