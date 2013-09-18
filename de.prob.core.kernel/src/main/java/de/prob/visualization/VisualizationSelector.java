package de.prob.visualization;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.servlets.visualizations.IVisualizationEngine;

@Singleton
public class VisualizationSelector {

	private static int ctr = 0;

	private final GroovyExecution e;

	@Inject
	public VisualizationSelector(final GroovyExecution e) {
		this.e = e;
	}

	public void registerSession(final String sessionId,
			final IVisualizationEngine servlet) {
		e.getBindings().setVariable("viz" + ctr++, servlet);
	}

}
