package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class WorksheetView extends ViewPart {

	public static final String ID = "de.prob.ui.console.GroovyConsole";

	private final int port;
	private Browser browser;

	private static WorksheetView instance;

	// FIXME GroovyConsole Viewpart should open url after complete
	// initialization of Jetty Server. Sometimes the server is to slow
	public WorksheetView() {
		port = WebConsole.getPort();
		WorksheetView.instance = this;
	}

	@Override
	public void createPartControl(Composite shell) {

		browser = new Browser(shell, SWT.NONE);
		browser.setUrl("http://localhost:" + port + "/sessions/Worksheet");

	}

	@Override
	public void setFocus() {
	}

	public static WorksheetView getInstance() {
		if (WorksheetView.instance == null)
			WorksheetView.instance = new WorksheetView();
		return WorksheetView.instance;
	}

}
