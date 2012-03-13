package de.prob.scripting;

import com.google.inject.Inject;

import de.prob.model.languages.ClassicalBFactory;

public class FactoryProvider {

	private final ClassicalBFactory classical_b_factory;
	private final CspFactory csp_factory;

	@Inject
	public FactoryProvider(final ClassicalBFactory bfactory,
			final CspFactory csp_factory) {
		this.classical_b_factory = bfactory;
		this.csp_factory = csp_factory;
	}

	public ClassicalBFactory getClassicalBFactory() {
		return classical_b_factory;
	}

	public CspFactory getCspFactory() {
		return csp_factory;
	}

}
