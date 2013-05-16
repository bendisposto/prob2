package de.prob.webconsole.servlets.visualizations;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.HTMLResources;
import de.prob.visualization.VisualizationSelector;

@SuppressWarnings("serial")
@Singleton
public class StateSpaceServlet extends SessionBasedServlet implements
		IModelChangedListener {

	private StateSpace currentStateSpace;
	private final VisualizationSelector visualizations;

	@Inject
	public StateSpaceServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations) {
		this.visualizations = visualizations;
		animations.registerModelChangedListener(this);
	}

	@Override
	public void modelChanged(final StateSpace s) {
		currentStateSpace = s;
	}

	public String openSession() throws AnimationNotLoadedException {
		if (currentStateSpace == null) {
			throw new AnimationNotLoadedException(
					"Could not start state space visualization because no animation is loaded");
		}
		StateSpaceSession session = new StateSpaceSession(currentStateSpace);
		String id = super.openSession(session);
		visualizations.registerSession(id, session);
		return id;
	}

	@Override
	protected String getHTML(final String id) {
		return HTMLResources.getSSVizHTML(id);
	}

	@Override
	protected String getSessionId() {
		return "space" + count++;
	}
}
