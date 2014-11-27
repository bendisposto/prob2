package de.prob.web.worksheet;

import groovy.lang.MissingPropertyException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import de.prob.web.WebUtils;

public class Groovy extends AbstractBox implements IBox {

	private String content = "";

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@Override
	public List<Object> render(@Nullable BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();
		if (snapshot != null)
			snapshot.restoreBindings(groovy);
		ArrayList<Object> res = new ArrayList<Object>();
		StringBuffer outputsb = new StringBuffer();
		Bindings bindings = groovy.getBindings(ScriptContext.GLOBAL_SCOPE);
		bindings.put("__console", outputsb);
		Object evaluationResult = "null";
		try {
			evaluationResult = groovy.eval(content);
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

	@Override
	public Object getAside(BindingsSnapshot previous_snapshot,
			BindingsSnapshot current_snapshot) {
		Predicate<Entry<String, Object>> p = new Predicate<Entry<String, Object>>() {
			@Override
			public boolean apply(@Nullable final Entry<String, Object> input) {
				return !input.getKey().startsWith("__");
			}
		};
		Comparator<Entry<String, Object>> comperator = new Comparator<Entry<String, Object>>() {
			@Override
			public int compare(final Entry<String, Object> o1,
					final Entry<String, Object> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		};
		ScriptEngine groovy = owner.getGroovy();
		Collection<Entry<String, Object>> bindings_global = Collections2
				.filter(groovy.getBindings(ScriptContext.GLOBAL_SCOPE)
						.entrySet(), p);
		Collection<Entry<String, Object>> bindings_local = Collections2.filter(
				groovy.getBindings(ScriptContext.ENGINE_SCOPE).entrySet(), p);
		List<Entry<String, Object>> vars = new ArrayList<Entry<String, Object>>();
		vars.addAll(bindings_local);
		vars.addAll(bindings_global);
		Collections.sort(vars, comperator);
		Function<Entry<String, Object>, Map<String, String>> toJson = new VariableDetailTransformer(
				previous_snapshot, current_snapshot);
		Collection<Map<String, String>> vars2 = Collections2.transform(vars,
				toJson);
		Collection<Map<String, String>> vars3 = Collections2.filter(vars2,
				new Predicate<Map<String, String>>() {
					@Override
					public boolean apply(
							@Nullable final Map<String, String> input) {
						return input != null;
					}
				});
		return WebUtils.wrap("cmd", "Worksheet.aside", "number", this.getId(),
				"aside", WebUtils.toJson(vars3));
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

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}

}
