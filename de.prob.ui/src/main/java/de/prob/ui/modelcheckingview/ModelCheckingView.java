package de.prob.ui.modelcheckingview;

import de.prob.ui.BrowserView;

public class ModelCheckingView extends BrowserView {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.model_check";

	@Override
	protected String getUrl() {
		return "sessions/ModelCheckingUI";
	}

}
