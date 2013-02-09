package de.prob.ui.worksheet.editors.browserFunction;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import de.prob.ui.worksheet.editors.Worksheet;

public class setSessionId extends BrowserFunction {
	private Worksheet editor;

	public setSessionId(Browser browser, String name, Worksheet editor) {
		super(browser, name);
		this.editor = editor;
	}

	@Override
	public Object function(Object[] arguments) {
		this.editor.setSessionID((String) arguments[0]);
		return null;
	}
}
