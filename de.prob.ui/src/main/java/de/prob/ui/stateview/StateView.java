package de.prob.ui.stateview;

import de.prob.ui.BrowserView;

public class StateView extends BrowserView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.StateView";

	@Override
	protected String getUrl() {
		return "sessions/StateInspector";
	}

}