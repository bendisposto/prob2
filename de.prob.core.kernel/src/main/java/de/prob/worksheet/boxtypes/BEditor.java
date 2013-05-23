package de.prob.worksheet.boxtypes;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.worksheet.DefaultEditor;
import de.prob.worksheet.EBoxTypes;
import de.prob.worksheet.WorkSheet;

public class BEditor extends DefaultEditor {

	public BEditor(String id, String text) {
		super(id, text);
		type = EBoxTypes.b;
	}

	@Override
	protected String evaluate(WorkSheet ws) {

		ScriptEngine groovy = ws.getGroovy();
		Object store = groovy.get("store");
		
		
		
		if (store instanceof Long) {
			Long storeid = (Long) store;
			
		}
		else {
			
		}

		String script = "trace.";

		return "";
		// new RenderResult(WorkSheet.RENDERER_TEMPLATE_SIMPLE_TEXT,
		// "GTFO! " + getText() + "\n Do I look like a calculator?");
	}
}
