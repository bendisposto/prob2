package de.prob.webconsole.servlets.visualizations;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.AnimationSelector;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.HTMLResources;
import de.prob.visualization.VisualizationSelector;

@Singleton
public class PredicateServlet extends SessionBasedServlet {

	private final AnimationSelector animations;
	private final VisualizationSelector visualizations;

	@Inject
	public PredicateServlet(final AnimationSelector animations,
			final VisualizationSelector visualizations) {
		this.visualizations = visualizations;
		this.animations = animations;
	}

	public String openSession(final IEvalElement formula)
			throws AnimationNotLoadedException {
		if (animations.getCurrentHistory() == null) {
			throw new AnimationNotLoadedException("Could not visualize "
					+ formula.getCode() + " because no animation is loaded");
		}
		PredicateSession session = new PredicateSession(animations, formula);
		String id = super.openSession(session);
		visualizations.registerSession(id, session);
		return id;
	}

	@Override
	protected String getHTML(final String id) {
		return HTMLResources.getPredicateHTML(id);
	}

	@Override
	protected String getSessionId() {
		return "pred" + count++;
	}
}
