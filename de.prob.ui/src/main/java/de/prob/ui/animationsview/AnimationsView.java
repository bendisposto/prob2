package de.prob.ui.animationsview;

import de.prob.ui.BrowserView;

public class AnimationsView extends BrowserView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.AnimationsView";

	@Override
	protected String getUrl() {
		return "sessions/CurrentAnimations";
	}

}