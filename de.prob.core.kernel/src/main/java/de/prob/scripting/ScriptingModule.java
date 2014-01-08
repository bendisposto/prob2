package de.prob.scripting;

import com.google.inject.AbstractModule;

import de.prob.testing.TestRunner;

public class ScriptingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Api.class);
		bind(Downloader.class);
		bind(ScriptEngineProvider.class);
		bind(TestRunner.class);
	}

}
