package de.prob.scripting;

import com.google.inject.Inject;

import de.prob.model.classicalb.ClassicalBFactory;


public class FactoryProvider {

	private final ClassicalBFactory classical_b_factory;
	private final CspFactory csp_factory;
	private final EventBFactory eventb_factory;

	@Inject
	public FactoryProvider(final ClassicalBFactory bfactory,
			final CspFactory csp_factory, EventBFactory eventb_factory) {
		this.classical_b_factory = bfactory;
		this.csp_factory = csp_factory;
		this.eventb_factory = eventb_factory;
	}

	public ClassicalBFactory getClassicalBFactory() {
		return classical_b_factory;
	}
	public EventBFactory getEventBFactory() {
		return eventb_factory;
	}

	public CspFactory getCspFactory() {
		return csp_factory;
	}

}
