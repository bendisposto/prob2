package de.prob.webconsole;

import com.google.inject.servlet.ServletModule;

import de.prob.webconsole.servlets.GroovyBindingsServlet;
import de.prob.webconsole.servlets.GroovyShellServlet;
import de.prob.webconsole.servlets.LogLevelServlet;
import de.prob.webconsole.servlets.VersionInfo;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/evaluate*").with(GroovyShellServlet.class);
		serve("/loglevel*").with(LogLevelServlet.class);
		serve("/bindings*").with(GroovyBindingsServlet.class);
		bind(VersionInfo.class).asEagerSingleton();
	}
}
