package de.prob.model;

import com.google.inject.AbstractModule;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		// install(new FactoryModuleBuilder()
		// .implement(Payment.class, RealPayment.class)
		// .build(PaymentFactory.class));

		bind(StateSpace.class);
		bind(DirectedSparseMultigraph.class);
	}
}
