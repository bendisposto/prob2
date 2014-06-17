package de.prob.web;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;

import de.prob.annotations.Sessions;
import de.prob.bmotion.BMotionStudioServlet;
import de.prob.webconsole.OutputBuffer;
import de.prob.webconsole.servlets.GroovyOutputServlet;
import de.prob.webconsole.servlets.ScrollbackServlet;
import de.prob.webconsole.servlets.VersionServlet;
import de.prob.webconsole.servlets.visualizations.StateSpaceServlet;

public class WebModule extends ServletModule {

	@Override
	protected void configureServlets() {
		super.configureServlets();
		serve("/outputs*").with(GroovyOutputServlet.class);
		serve("/versions*").with(VersionServlet.class);
		serve("/scrollback*").with(ScrollbackServlet.class);

		bind(OutputBuffer.class);

		TypeLiteral<Map<String, ISession>> mapType = new TypeLiteral<Map<String, ISession>>() {
		};

		bind(mapType).annotatedWith(Sessions.class).toInstance(
				new HashMap<String, ISession>());

		serve("/statespace_servlet*").with(StateSpaceServlet.class);
		// filter("/sessions/*").through(ReflectorFilter.class);

		serve(ReflectionServlet.URL_PATTERN + "*")
				.with(ReflectionServlet.class);

		serve("/files*").with(FileBrowserServlet.class);
		serve("/ws/*").with(WorkspaceServlet.class);
		serve("/bms/*").with(BMotionStudioServlet.class);
		serve("/data*").with(DataServlet.class);
		serve("/content*").with(ContentServlet.class);

	}

	@Provides
	public UUID createUUID() {
		return UUID.randomUUID();
	}
}
