package de.prob.ui.worksheet.editors.browserFunction;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import de.prob.ui.worksheet.editors.Worksheet;

public class setDirty extends BrowserFunction {
	private Worksheet editor;

	public setDirty(Browser browser, String name, Worksheet editor) {
		super(browser, name);
		this.editor = editor;
	}

	@Override
	public Object function(Object[] arguments) {
		editor.setDirty(((Boolean) arguments[0]).booleanValue());
		return super.function(arguments);
	}
}
