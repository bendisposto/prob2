package de.prob.scripting;

import com.google.inject.AbstractModule;

import de.prob.testing.ProBTestRunner;
import de.prob.testing.TestRegistry;

public class ScriptingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Api.class);
		bind(Downloader.class);
		bind(ScriptEngineProvider.class);
		bind(ProBTestRunner.class);
		bind(TestRegistry.class);
	}

}
