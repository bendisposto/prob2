package de.prob.worksheet.boxtypes;

import de.prob.worksheet.DefaultEditor;
import de.prob.worksheet.EBoxTypes;
import de.prob.worksheet.WorkSheet;

public class MarkdownEditor extends DefaultEditor {

	public MarkdownEditor(String id, String text) {
		super(id, text);
		type = EBoxTypes.markdown;
	}

	@Override
	protected String evaluate(WorkSheet ws) {
		return ws.getPegdown().markdownToHtml(getText());
	}
}
