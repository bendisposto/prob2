package de.prob.ui.bmsview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ui.view.ProB2ViewUtil;

public class EditBMotionTemplateHandler extends AbstractHandler implements
		IHandler {

	Logger logger = LoggerFactory.getLogger(OpenBMotionTemplateHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

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
			ProB2ViewUtil.createProB2ViewPart("bms/?template=" + selected
					+ "&editor", BmsEditView.ID);
		} catch (PartInitException e) {
			logger.error("Could not open BMotion Studio editor: "
					+ e.getMessage());
		}

		return null;

	}

}
