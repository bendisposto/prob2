package de.prob.ui.bunitview;

import de.prob.ui.BrowserView;

public class BUnitView extends BrowserView {

	public static final String ID = "de.prob.ui.bunit.BUnitView";

	@Override
	protected String getUrl() {
		return "sessions/BUnit";
	}

}