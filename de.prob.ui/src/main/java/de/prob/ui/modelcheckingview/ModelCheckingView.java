package de.prob.ui.modelcheckingview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class ModelCheckingView extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.modelcheckingview.ModelCheckingView";
	private final int port;
	private Browser browser;

	public ModelCheckingView() {
		port = WebConsole.getPort();
	}

	@Override
	public void createPartControl(final Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl("http://localhost:" + port + "/sessions/ModelCheckingUI");
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

}
