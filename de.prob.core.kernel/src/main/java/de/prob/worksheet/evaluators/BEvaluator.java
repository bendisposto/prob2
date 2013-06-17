package de.prob.worksheet.evaluators;

import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreEvalCommand;
import de.prob.animator.command.EvalstoreEvalCommand.EvalstoreResult;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.AbstractModel;
import de.prob.worksheet.IEvaluator;
import de.prob.worksheet.WorkSheet;

public class BEvaluator implements IEvaluator {

	@Override
	public Object evaluate(WorkSheet context, String content) {
		ScriptEngine groovy = context.getGroovy();
		Object store = groovy.get("store");
		AbstractModel model = (AbstractModel) groovy.get("model");
		if (store instanceof Long) {
			Long storeid = (Long) store;
			EvalstoreEvalCommand c = new EvalstoreEvalCommand(storeid,
					new ClassicalB(content));
			model.getStatespace().execute(c);
			EvalstoreResult r = c.getResult();
			String value = r.getResult().value;
			return value;
		} else {
			return new IllegalStateException(
					"*Could not find evaluation context. Maybe you need to copy it from a trace*");
		}
	}

}
