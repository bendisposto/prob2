package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class GroovyConsole extends ViewPart {

	private final int port;

	public GroovyConsole() {
		port = WebConsole.getPort();
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

		Browser browser = new Browser(sashForm, SWT.NONE);
		browser.setUrl("http://localhost:" + port + "/console.jsp");
		Browser browser_1 = new Browser(sashForm, SWT.NONE);
		browser_1.setUrl("http://localhost:" + port + "/sysout.jsp");

		browser.setLayoutData(gridData);
		browser_1.setLayoutData(gridData);
		sashForm.setLayoutData(gridData);
		
		
	}

	@Override
	public void setFocus() {

	}

}
