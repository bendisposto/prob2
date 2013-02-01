package de.prob.scripting;

import com.google.inject.Inject;

/**
 * Provides the user with access to the {@link ClassicalBFactory},
 * {@link CSPFactory}, and {@link EventBFactory} objects that are injected into
 * the FactoryProvider
 * 
 * @author joy
 * 
 */
public class FactoryProvider {

	private final ClassicalBFactory classical_b_factory;
	private final CSPFactory csp_factory;
	private final EventBFactory eventb_factory;

	@Inject
	public FactoryProvider(final ClassicalBFactory bfactory,
			final CSPFactory csp_factory, final EventBFactory eventb_factory) {
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

	public CSPFactory getCspFactory() {
		return csp_factory;
	}

}
