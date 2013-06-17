package de.prob.worksheet.evaluators;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import de.prob.worksheet.IEvaluator;
import de.prob.worksheet.WorkSheet;

public class GroovyEvaluator implements IEvaluator {

	@Override
	public Object evaluate(WorkSheet context, String content) {
		ScriptEngine groovy = context.getGroovy();
		try {
			Object result = groovy.eval(content);
			return new GroovyResult(result.toString(), "not yet implemented");
		} catch (ScriptException e) {
			return e;
		}
	}
}
