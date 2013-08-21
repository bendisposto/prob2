package de.prob.web.worksheet.boxes;

import groovy.lang.MissingPropertyException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import de.prob.web.WebUtils;
import de.prob.web.views.IBox;

public class Groovy extends AbstractBox implements IBox {

	private String content = "";

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@Override
	public List<Object> render() {
		ArrayList<Object> res = new ArrayList<Object>();
		StringBuffer outputsb = new StringBuffer();
		Bindings bindings = owner.groovy
				.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("__console", outputsb);
		Object evaluationResult = "null";
		try {
			evaluationResult = owner.groovy.eval(content);
			String result = StringEscapeUtils.escapeHtml(evaluationResult
					.toString());
			String output = StringEscapeUtils.escapeHtml(outputsb.toString())
					.replaceAll("\n", "<br />");

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

	@Override
	protected String getContentAsJson() {
		return content;
	}

}
