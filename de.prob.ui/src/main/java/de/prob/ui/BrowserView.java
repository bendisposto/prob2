package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public abstract class BrowserView extends ViewPart {

	private final int port;
	private Browser browser;

	public BrowserView() {
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl("http://localhost:" + port + "/sessions/" + getUrl());
	}

	protected abstract String getUrl();

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		browser.setFocus();
	}

}