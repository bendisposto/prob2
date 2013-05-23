package de.prob.worksheet;

import javax.script.ScriptEngine;

import org.pegdown.PegDownProcessor;

import de.prob.model.classicalb.ClassicalBModel;
import de.prob.scripting.Api;

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
			groovy.put("model", b_load);
			groovy.eval("trace = model as History");
			return "Successfully loaded "
					+ getText()
					+ ".\n Stored model in variable 'model' and a trace in variable 'trace'";
		} catch (Exception e) {
			return pegdown.markdownToHtml("          "
					+ e.getMessage().replaceAll("\n", "\n        "));
		}
	}

}
