package de.prob.ui.console;

import de.prob.ui.BrowserView;

public class WorksheetView extends BrowserView {

	public static final String ID = "de.prob.ui.console.GroovyConsole";

	private static WorksheetView instance;

	// FIXME GroovyConsole Viewpart should open url after complete
	// initialization of Jetty Server. Sometimes the server is to slow
	public WorksheetView() {
		WorksheetView.instance = this;
	}

	public static WorksheetView getInstance() {
		if (WorksheetView.instance == null)
			WorksheetView.instance = new WorksheetView();
		return WorksheetView.instance;
	}

	@Override
	protected String getUrl() {
		return "sessions/Worksheet";
	}

}
