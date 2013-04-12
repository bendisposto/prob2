package de.prob.ui.visualization;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class VizView extends ViewPart {

	public static final String ID = "de.prob.ui.viz.VizView";

	private final int port;
	private Browser browser;

	private String sessionId;

	public VizView() {
		port = WebConsole.getPort();
	}

	@Override
	public void createPartControl(final Composite shell) {
		GridLayout gl_shell = new GridLayout(1, true);
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		browser = new Browser(sashForm, SWT.NONE);

		browser.setLayoutData(gridData);
		sashForm.setLayoutData(gridData);

	}

	public Browser getBrowser() {
		return browser;
	}

	public void init(final String url) {
		browser.setUrl("http://localhost:" + port + "/" + url);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void dispose() {

		if (browser != null) {
			browser.dispose();
		}
		super.dispose();
	}

}
