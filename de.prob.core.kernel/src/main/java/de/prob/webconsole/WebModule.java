package de.prob.webconsole;

import com.google.inject.servlet.ServletModule;

import de.prob.webconsole.servlets.EvaluationServlet;
import de.prob.webconsole.servlets.LogLevelServlet;
import de.prob.webconsole.servlets.VersionInfo;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/evaluate*").with(EvaluationServlet.class);
		serve("/loglevel*").with(LogLevelServlet.class);
		bind(VersionInfo.class).asEagerSingleton();
	}
}
