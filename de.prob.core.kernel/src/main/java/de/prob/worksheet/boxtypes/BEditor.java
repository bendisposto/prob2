package de.prob.worksheet.boxtypes;

import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.AbstractModel;
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
		AbstractModel model = (AbstractModel) groovy.get("model");

		if (store instanceof Long) {
			Long storeid = (Long) store;
			EvalstoreEvalCommand c = new EvalstoreEvalCommand(storeid,
					new ClassicalB(getText()));
			model.getStatespace().execute(c);
			EvalstoreResult r = c.getResult();
			String value = r.getResult().value;
			return value;
		} else {
			return ws
					.getPegdown()
					.markdownToHtml(
							"*Could not find evaluation context. Maybe you need to copy it from a trace*");

		}

	}
}
