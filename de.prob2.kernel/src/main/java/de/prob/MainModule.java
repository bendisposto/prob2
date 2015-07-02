package de.prob;

import com.google.inject.AbstractModule;

import de.prob.animator.AnimatorModule;
import de.prob.cli.ModuleCli;
import de.prob.scripting.ScriptingModule;
import de.prob.statespace.ModelModule;

/**
 * This Guice {@link AbstractModule} contains all the configuration information
 * necessary to configure ProB 2.0.
 * 
 * @author joy
 * 
 */
public class MainModule extends AbstractModule {

	@Override
	protected final void configure() {
		install(new MainConfiguration());
		install(new ModuleCli());
		install(new AnimatorModule());
		install(new ModelModule());
		install(new ScriptingModule());
	}
}
