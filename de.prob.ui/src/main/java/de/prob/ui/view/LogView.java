package de.prob.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class LogView extends ViewPart {

	public static final String ID = "de.prob.ui.log.LogView";

	private final int port;
	private Browser browser;

	private static LogView instance;

	public LogView() {
		port = WebConsole.getPort();
		LogView.instance = this;
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
		browser.setUrl("http://localhost:" + port + "/sessions/Log");

		browser.setLayoutData(gridData);
		sashForm.setLayoutData(gridData);

	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public static LogView getInstance() {
		if (LogView.instance == null) {
			LogView.instance = new LogView();
		}
		return LogView.instance;
	}

}
