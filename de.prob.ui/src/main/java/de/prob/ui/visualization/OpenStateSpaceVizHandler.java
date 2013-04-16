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
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.servlets.StateSpaceServlet;

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
			String sessionId = servlet.openSession();
			VisualizationUtil.createVisualizationViewPart(sessionId,
					"statespace_servlet/?init=" + sessionId);
		} catch (PartInitException e) {
			logger.error("Could not create predicate visualization view: "
					+ e.getMessage());
		} catch (AnimationNotLoadedException e) {
			logger.error("Could not create predicate visualization because an animation is not loaded: "
					+ e.getMessage());
		}
		return null;
	}

}
