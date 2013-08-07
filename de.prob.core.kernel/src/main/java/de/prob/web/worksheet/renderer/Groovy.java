package de.prob.web.worksheet.renderer;

import groovy.lang.MissingPropertyException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import de.prob.web.WebUtils;
import de.prob.web.views.Worksheet;

public class Groovy extends AbstractRenderer {

	@Override
	public List<Object> render(String id, String text, Worksheet worksheet) {
		ArrayList<Object> res = new ArrayList<Object>();
		StringBuffer output = new StringBuffer();
		Bindings bindings = worksheet.groovy
				.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("__console", output);
		Object result = "null";
		try {
			result = worksheet.groovy.eval(text);
			res.add(makeHtml(
					id,
					WebUtils.render("ui/worksheet/groovy_box.html", WebUtils
							.wrap("id", id, "result", result, "output", output))));
		} catch (ScriptException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			res.add(makeHtml(id, WebUtils.render(
					"ui/worksheet/groovy_exception.html", WebUtils.wrap("id",
							id, "message", cleanup(e), "stacktrace", sw
									.toString().replaceAll(" ", "&nbsp;")
									.replaceAll("\\n", "<br />")))));
		}
		return res;
	}

	private String cleanup(Exception e) {
		String message = e.getMessage();
		if (e.getCause() instanceof MultipleCompilationErrorsException)

			return message.replaceAll("(.*\n.*Script.*?groovy):.*?:", "")
					.replaceAll("@ line(.*\n)*", "").replaceAll(" ", "&nbsp;")
					.replaceAll("\\n", "<br />");
		;
		if (e.getCause().getCause() instanceof MissingPropertyException) {
			String r1 = message.replaceAll(".*property:", "No such property: ");
			String r2 = r1.replaceAll("for.*", "");
			return r2;
		}
		return message.replaceAll(" ", "&nbsp;").replaceAll("\\n", "<br />");
	}

}
