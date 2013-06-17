package de.prob.worksheet;

import javax.script.ScriptEngine;

import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class LoadEvaluator implements IEvaluator {

	private Api api;

	public LoadEvaluator(Api api) {
		this.api = api;
	}

	@Override
	public Object evaluate(WorkSheet context, String content) {
		try {
			ClassicalBModel b_load = api.b_load(content);
			ScriptEngine groovy = context.getGroovy();
			StateSpace statespace = b_load.getStatespace();
			Trace t = new Trace(statespace);
			EvalstoreCreateByStateCommand c = new EvalstoreCreateByStateCommand(
					"root");
			statespace.execute(c);
			long store = c.getEvalstoreId();
			groovy.put("model", b_load);
			groovy.put("trace", t);
			groovy.put("store", store);
			return "Successfully loaded "
					+ content
					+ ".\n Stored model in variable 'model' and a trace in variable 'trace'";
		} catch (Exception e) {
			return e;
		}
	}

}
