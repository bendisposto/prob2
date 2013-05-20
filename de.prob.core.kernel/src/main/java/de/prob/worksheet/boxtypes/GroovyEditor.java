package de.prob.worksheet.boxtypes;

import groovy.lang.MissingPropertyException;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.pegdown.PegDownProcessor;

import de.prob.worksheet.DefaultEditor;
import de.prob.worksheet.EBoxTypes;
import de.prob.worksheet.WorkSheet;

public class GroovyEditor extends DefaultEditor {

	public GroovyEditor(String id, String text) {
		super(id, text);
		type = EBoxTypes.groovy;
	}

	@Override
	protected String evaluate(WorkSheet ws) {
		ScriptEngine groovy = ws.getGroovy();
		PegDownProcessor pegdown = ws.getPegdown();
		Object result = null;
		try {
			result = groovy.eval(getText());
		} catch (ScriptException e) {
			return pegdown.markdownToHtml("          "
					+ cleanGroovyException(e).replaceAll("\n", "\n        "));
		}
		return result == null ? "null" : result.toString();
	}

	private String cleanGroovyException(ScriptException e) {
		String message = e.getMessage();
		if (e.getCause() instanceof MultipleCompilationErrorsException)
			return message.replaceAll("(.*\n.*Script.*?groovy): ", "");
		if (e.getCause().getCause() instanceof MissingPropertyException) {
			String r1 = message.replaceAll(".*property:", "No such property: ");
			String r2 = r1.replaceAll("for.*", "");
			return r2;
		}
		return message;
	}

}
