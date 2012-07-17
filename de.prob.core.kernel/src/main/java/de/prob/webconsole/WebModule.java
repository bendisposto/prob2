package de.prob.webconsole;

import com.google.inject.servlet.ServletModule;

import de.prob.webconsole.servlets.CompletionServlet;
import de.prob.webconsole.servlets.GroovyBindingsServlet;
import de.prob.webconsole.servlets.GroovyOutputServlet;
import de.prob.webconsole.servlets.GroovyShellServlet;
import de.prob.webconsole.servlets.ImportsServlet;
import de.prob.webconsole.servlets.LogLevelServlet;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/evaluate*").with(GroovyShellServlet.class);
		serve("/loglevel*").with(LogLevelServlet.class);
		serve("/bindings*").with(GroovyBindingsServlet.class);
		serve("/complete*").with(CompletionServlet.class);
		serve("/imports*").with(ImportsServlet.class);
		serve("/outputs*").with(GroovyOutputServlet.class);
	}
}
