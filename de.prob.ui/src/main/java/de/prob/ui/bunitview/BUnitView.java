package de.prob.ui.bunitview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class BUnitView extends ViewPart {

	public static final String ID = "de.prob.ui.bunit.BUnitView";

	private final int port;

	private Browser browser;

	public BUnitView() {
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		browser = new Browser(parent, SWT.None);
		browser.setUrl("http://localhost:" + port + "/sessions/BUnit");
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

}