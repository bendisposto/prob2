package de.prob.ui.visualization;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.ui.PartInitException;

public class OpenVizHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String parameter = event
		.getParameter("de.prob.ui.viz.name");
		
		try {
			VisualizationUtil.createVisualizationViewPart(parameter);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
