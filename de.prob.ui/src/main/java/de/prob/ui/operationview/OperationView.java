package de.prob.ui.operationview;

import de.prob.ui.BrowserView;

public class OperationView extends BrowserView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	@Override
	protected String getUrl() {
		return "sessions/Events";
	}

}