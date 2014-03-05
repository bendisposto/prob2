package de.prob.ui.visualization;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenBMotionTemplateHandler extends AbstractHandler implements
		IHandler {

	Logger logger = LoggerFactory.getLogger(OpenBMotionTemplateHandler.class);

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		
		// File standard dialog
		FileDialog fileDialog = new FileDialog(Display.getDefault()
				.getActiveShell());
		// Set the text
		fileDialog.setText("Select File");
		// Set filter on .txt files
		fileDialog.setFilterExtensions(new String[] { "*.html" });
		// Put in a readable name for the filter
		fileDialog.setFilterNames(new String[] { "Html Files (*.html)" });
		// Open Dialog and save result of selection
		String selected = fileDialog.open();
				
		try {
			VisualizationUtil.createVisualizationViewPart(
					"bms/?template=" + selected, "de.prob.ui.BMotionView");
		} catch (PartInitException e) {
			logger.error("Could not create BMotion Studio visualization view: "
					+ e.getMessage());
		}
		
		return null;
		
	}

}
