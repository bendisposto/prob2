package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.visualization.AnimationNotLoadedException;
import de.prob.visualization.VisualizationException;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.servlets.visualizations.StateSpaceServlet;
import de.prob.webconsole.servlets.visualizations.StateSpaceSession;

public class OpenStateSpaceVizHandler extends AbstractHandler implements
		IHandler {

	Logger logger = LoggerFactory.getLogger(OpenStateSpaceVizHandler.class);

	private final StateSpaceServlet servlet;

	public OpenStateSpaceVizHandler() {
		servlet = ServletContextListener.INJECTOR
				.getInstance(StateSpaceServlet.class);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);

		try {
			String sessionId = VisualizationUtil.createSessionId();
			servlet.openSession(sessionId);

			StateSpaceSession sessionServlet = servlet
					.getSessionServlet(sessionId);
			VizView vizView = VisualizationUtil
					.createVisualizationViewPart("statespace_servlet/?init="
							+ sessionId);
			sessionServlet.registerRefreshListener(vizView);
		} catch (PartInitException e) {
			logger.error("Could not create state space visualization view: "
					+ e.getMessage());
		} catch (AnimationNotLoadedException e) {
			logger.error("Could not create state space visualization because an animation is not loaded: "
					+ e.getMessage());
		} catch (VisualizationException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

}
