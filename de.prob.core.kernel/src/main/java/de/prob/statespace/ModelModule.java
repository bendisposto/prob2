package de.prob.statespace;

import java.util.Random;

import com.google.inject.AbstractModule;

import de.prob.model.classicalb.ClassicalBModel;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ModelModule extends AbstractModule {

	@Override
	protected void configure() {
		// install(new FactoryModuleBuilder()
		// .implement(Payment.class, RealPayment.class)
		// .build(PaymentFactory.class));

		bind(StateSpace.class);
		bind(DirectedSparseMultigraph.class);
		bind(Random.class);
		bind(History.class);
		bind(StateSpaceInfo.class);
		bind(ClassicalBModel.class);
	}
}
