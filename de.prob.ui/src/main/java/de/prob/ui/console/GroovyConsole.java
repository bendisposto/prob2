package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class GroovyConsole extends ViewPart {

	public static final String ID = "de.prob.ui.groovyconsole";

	private final int port;
	private Browser consoleBrowser;

	private static GroovyConsole instance;

	// FIXME GroovyConsole Viewpart should open url after complete
	// initialization of Jetty Server. Sometimes the server is to slow
	public GroovyConsole() {
		port = WebConsole.getPort();
		GroovyConsole.instance = this;
	}

	@Override
	public void createPartControl(Composite shell) {
		GridLayout gl_shell = new GridLayout(1, true);
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		consoleBrowser = new Browser(shell, SWT.NONE);
		consoleBrowser
				.setUrl("http://localhost:8080/sessions/de.prob.web.views.GroovyConsoleSession");

		consoleBrowser.setLayoutData(gridData);
		shell.setLayoutData(gridData);

	}

	public Browser getConsoleBrowser() {
		return consoleBrowser;
	}

	@Override
	public void setFocus() {
	}

	public static GroovyConsole getInstance() {
		if (GroovyConsole.instance == null) {
			GroovyConsole.instance = new GroovyConsole();
		}
		return GroovyConsole.instance;
	}

}
