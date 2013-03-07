package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class GroovyConsole extends ViewPart {

	public static final String ID = "de.prob.ui.console.GroovyConsole";

	private final int port;
	private Browser consoleBrowser;
	private Browser outputBrowser;

	private static GroovyConsole instance;

	public GroovyConsole() {
		port = WebConsole.getPort();
		instance = this;
	}

	@Override
	public void createPartControl(Composite shell) {
		GridLayout gl_shell = new GridLayout(1, true);
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		consoleBrowser = new Browser(sashForm, SWT.NONE);
		consoleBrowser.setUrl("http://localhost:" + port + "/console.html");
		outputBrowser = new Browser(sashForm, SWT.NONE);
		outputBrowser.setUrl("http://localhost:" + port + "/sysout.html");

		consoleBrowser.setLayoutData(gridData);
		outputBrowser.setLayoutData(gridData);
		sashForm.setLayoutData(gridData);

	}

	public Browser getConsoleBrowser() {
		return consoleBrowser;
	}

	public Browser getOutputBrowser() {
		return outputBrowser;
	}

	@Override
	public void setFocus() {
	}

	public static GroovyConsole getInstance() {
		if (instance == null)
			instance = new GroovyConsole();
		return instance;
	}

}
