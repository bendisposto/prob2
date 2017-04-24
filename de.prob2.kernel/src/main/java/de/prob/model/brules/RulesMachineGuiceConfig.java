package de.prob.model.brules;

import com.google.inject.AbstractModule;

import de.prob.MainModule;

public class RulesMachineGuiceConfig extends AbstractModule {

	@Override
	protected void configure() {
		install(new MainModule()); // Install ProB 2.0 Injection bindings
	}

}
