package de.prob.webconsole.servlets.visualizations;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.HTMLResources;
import de.prob.visualization.VisualizationSelector;

@SuppressWarnings("serial")
@Singleton
public class ValueOverTimeServlet extends SessionBasedServlet {

	private final AnimationSelector animations;
	private final VisualizationSelector visualizations;

	@Inject
	public ValueOverTimeServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations) {
		this.animations = animations;
		this.visualizations = visualizations;
	}

	public String openSession(final IEvalElement formula)
			throws AnimationNotLoadedException {
		if (animations.getCurrentHistory() == null) {
			throw new AnimationNotLoadedException("Could not visualize "
					+ formula.getCode() + " because no animation is loaded");
		}
		ValueOverTimeSession session = new ValueOverTimeSession(formula,
				animations);
		String sessionId = super.openSession(session);
		visualizations.registerSession(sessionId, session);
		return sessionId;
	}

	@Override
	protected String getHTML(final String id) {
		return HTMLResources.getValueVsTimeHTML(id);
	}

	@Override
	protected String getSessionId() {
		return "value" + count++;
	}

}
