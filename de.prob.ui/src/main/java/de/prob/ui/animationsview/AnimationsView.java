package de.prob.ui.animationsview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class AnimationsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.AnimationsView";

	private Browser browser;
	private final int port;

	public AnimationsView() {
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		browser = new Browser(parent, SWT.None);
		browser.setUrl("http://localhost:" + port
				+ "/sessions/CurrentAnimations");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		browser.setFocus();
	}
}