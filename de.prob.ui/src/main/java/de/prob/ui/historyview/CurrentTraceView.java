package de.prob.ui.historyview;

import de.prob.ui.BrowserView;

public class CurrentTraceView extends BrowserView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.historyview.CurrentTraceView";

	@Override
	protected String getUrl() {
		return "sessions/CurrentTrace";
	}
}