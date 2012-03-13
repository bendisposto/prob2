package de.prob.model;

import com.google.inject.AbstractModule;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		// install(new FactoryModuleBuilder()
		// .implement(Payment.class, RealPayment.class)
		// .build(PaymentFactory.class));

		bind(StateSpace.class);
	}
}
