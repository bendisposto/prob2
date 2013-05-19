package de.prob.worksheet.boxtypes;

import de.prob.worksheet.DefaultEditor;
import de.prob.worksheet.EBoxTypes;
import de.prob.worksheet.RenderResult;
import de.prob.worksheet.WorkSheet;

public class BEditor extends DefaultEditor {

	public BEditor(String id, String text) {
		super(id, text);
		type = EBoxTypes.b;
	}

	@Override
	protected RenderResult evaluate(WorkSheet ws) {
		return new RenderResult(WorkSheet.RENDERER_TEMPLATE_SIMPLE_TEXT,
				"GTFO! " + getText() + "\n Do I look like a calculator?");
	}

}
