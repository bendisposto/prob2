package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.visualization.AnimationNotLoadedException;

public class OpenBmsViewHandler extends AbstractHandler implements
		IHandler {

	Logger logger = LoggerFactory.getLogger(OpenBmsViewHandler.class);

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			VisualizationUtil.createVisualizationViewPart(
					"bms/", "de.prob.ui.BMotionView");
		} catch (PartInitException e) {
			logger.error("Could not create bmotion studi visualization view: "
					+ e.getMessage());
		} catch (AnimationNotLoadedException e) {
			logger.error("Could not create bmotion studio visualization because an animation is not loaded: "
					+ e.getMessage());
		}
		return null;
	}

}
