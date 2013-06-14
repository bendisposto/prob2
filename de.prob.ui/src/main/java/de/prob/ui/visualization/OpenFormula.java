package de.prob.ui.visualization;

import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.VisualizationException;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.servlets.visualizations.PredicateServlet;

public class OpenFormula {

	Logger logger = LoggerFactory.getLogger(OpenFormula.class);

	private final PredicateServlet servlet;

	public OpenFormula() {
		servlet = ServletContextListener.INJECTOR
				.getInstance(PredicateServlet.class);
	}

	public void run(final IEvalElement formula) {
		try {
			String sessionId = VisualizationUtil.createSessionId();
			servlet.openSession(sessionId, formula);
			VisualizationUtil.createVisualizationViewPart("predicate/?init="
					+ sessionId);
		} catch (PartInitException e) {
			logger.error("Could not create predicate visualization view: "
					+ e.getMessage());
		} catch (AnimationNotLoadedException e) {
			logger.error("Could not create predicate visualization because an animation is not loaded: "
					+ e.getMessage());
		} catch (VisualizationException e) {
			logger.error(e.getMessage());
		}
	}

}
