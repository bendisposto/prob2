package de.prob.webconsole;

import com.google.inject.servlet.ServletModule;

import de.prob.testing.ProBTestRunner;
import de.prob.testing.TestRegistry;
import de.prob.webconsole.servlets.CompletionServlet;
import de.prob.webconsole.servlets.FormulaOverHistoryServlet;
import de.prob.webconsole.servlets.GroovyBindingsServlet;
import de.prob.webconsole.servlets.GroovyOutputServlet;
import de.prob.webconsole.servlets.GroovyShellServlet;
import de.prob.webconsole.servlets.ImportsServlet;
import de.prob.webconsole.servlets.ScrollbackServlet;
import de.prob.webconsole.servlets.VersionServlet;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/evaluate*").with(GroovyShellServlet.class);
		serve("/bindings*").with(GroovyBindingsServlet.class);
		serve("/complete*").with(CompletionServlet.class);
		serve("/imports*").with(ImportsServlet.class);
		serve("/outputs*").with(GroovyOutputServlet.class);
		serve("/versions*").with(VersionServlet.class);
		serve("/scrollback*").with(ScrollbackServlet.class);
		serve("/formula*").with(FormulaOverHistoryServlet.class);
		bind(ShellCommands.class);
		bind(OutputBuffer.class);
		bind(ProBTestRunner.class);
		bind(TestRegistry.class);
	}
}
