package de.prob.worksheet.boxtypes;

import javax.script.ScriptEngine;

import org.pegdown.PegDownProcessor;

import de.prob.animator.command.EvalstoreCreateByStateCommand;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.worksheet.DefaultEditor;
import de.prob.worksheet.EBoxTypes;
import de.prob.worksheet.WorkSheet;

public class LoadModelEditor extends DefaultEditor {

	private Api api;
	private ClassicalBModel b_load;

	public LoadModelEditor(String id, String text, Api api) {
		super(id, text);
		this.api = api;
		type = EBoxTypes.load;
	}

	@Override
	protected String evaluate(WorkSheet ws) {
		PegDownProcessor pegdown = ws.getPegdown();
		try {
			b_load = api.b_load(getText());
			ScriptEngine groovy = ws.getGroovy();
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
					+ getText()
					+ ".\n Stored model in variable 'model' and a trace in variable 'trace'";
		} catch (Exception e) {
			return pegdown.markdownToHtml("          "
					+ e.getMessage().replaceAll("\n", "\n        "));
		}
	}
}
