package de.prob.scripting;

import com.google.inject.AbstractModule;

public class ScriptingModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Api.class);
		bind(Downloader.class);
		bind(ScriptEngineProvider.class);
	}

}
