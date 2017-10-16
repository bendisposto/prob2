package de.prob.scripting;

import com.google.inject.Inject;

import de.prob.model.brules.RulesModelFactory;

/**
 * Provides the user with access to the {@link ClassicalBFactory},
 * {@link CSPFactory}, and {@link EventBFactory} objects that are injected into
 * the FactoryProvider
 * 
 * @author joy
 * 
 */
public class FactoryProvider {

	private final ClassicalBFactory classicalBFactory;
	private final CSPFactory cspFactory;
	private final EventBFactory eventBFactory;
	private final TLAFactory tlaFactory;
	private final RulesModelFactory bRulesFactory;

	@Inject
	public FactoryProvider(final ClassicalBFactory bfactory, final CSPFactory cspFactory,
			final EventBFactory eventBFactory, final TLAFactory tlaFactory, final RulesModelFactory bRulesFactory) {
		this.classicalBFactory = bfactory;
		this.cspFactory = cspFactory;
		this.eventBFactory = eventBFactory;
		this.tlaFactory = tlaFactory;
		this.bRulesFactory = bRulesFactory;
	}

	public ClassicalBFactory getClassicalBFactory() {
		return classicalBFactory;
	}

	public EventBFactory getEventBFactory() {
		return eventBFactory;
	}

	public CSPFactory getCspFactory() {
		return cspFactory;
	}

	public TLAFactory getTLAFactory() {
		return tlaFactory;
	}

	public RulesModelFactory getBRulesFactory() {
		return bRulesFactory;
	}

}
