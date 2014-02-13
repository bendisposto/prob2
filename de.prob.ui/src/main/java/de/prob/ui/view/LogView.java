package de.prob.ui.view;

import de.prob.ui.BrowserView;

public class LogView extends BrowserView {

	public static final String ID = "de.prob.ui.log.LogView";

	private static LogView instance;

	public LogView() {
		LogView.instance = this;
	}

	public static LogView getInstance() {
		if (LogView.instance == null) {
			LogView.instance = new LogView();
		}
		return LogView.instance;
	}

	@Override
	protected String getUrl() {
		return "Log";
	}

}
