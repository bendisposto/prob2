package de.prob.ui.console;

import de.prob.ui.BrowserView;

public class GroovyConsole extends BrowserView {

	public static final String ID = "de.prob.ui.groovyconsole";

	private static GroovyConsole instance;

	// FIXME GroovyConsole Viewpart should open url after complete
	// initialization of Jetty Server. Sometimes the server is to slow
	public GroovyConsole() {
		GroovyConsole.instance = this;
	}

	public static GroovyConsole getInstance() {
		if (GroovyConsole.instance == null) {
			GroovyConsole.instance = new GroovyConsole();
		}
		return GroovyConsole.instance;
	}

	@Override
	protected String getUrl() {
		return "sessions/GroovyConsoleSession";
	}

}
