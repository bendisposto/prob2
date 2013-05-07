package de.prob.ui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class Worksheet extends ViewPart {

	public static final String ID = "de.prob.ui.console.GroovyConsole";

	private final int port;
	private Browser browser;

	private static Worksheet instance;

	// FIXME GroovyConsole Viewpart should open url after complete
	// initialization of Jetty Server. Sometimes the server is to slow
	public Worksheet() {
		port = WebConsole.getPort();
		Worksheet.instance = this;
	}

	@Override
	public void createPartControl(Composite shell) {

		browser = new Browser(shell, SWT.NONE);
		browser.setUrl("http://localhost:" + port);

	}

	@Override
	public void setFocus() {
	}

	public static Worksheet getInstance() {
		if (Worksheet.instance == null)
			Worksheet.instance = new Worksheet();
		return Worksheet.instance;
	}

}
