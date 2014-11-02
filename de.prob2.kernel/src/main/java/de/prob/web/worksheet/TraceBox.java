package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.web.WebUtils;

public class TraceBox extends AbstractBox {
	Logger logger = LoggerFactory.getLogger(TraceBox.class);
	private String content = "";
	private String trace;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		List<Object> res = new ArrayList<Object>();
		if (this.trace == null) {
			Map<String, String> renderMap = makeHtml(id,
					"Kein Trace ausgew&auml;lt");
			res.add(renderMap);
		} else {
			ScriptEngine groovy = owner.getGroovy();
			if (snapshot != null)
				snapshot.restoreBindings(groovy);
			if (this.content != null) {
				String script = this.trace + "=execTrace(" + this.trace + "){"
						+ this.content + "};";
				try {
					groovy.eval(script);
				} catch (ScriptException e) {
					logger.error("Not able to execute script " + this.content
							+ " for trace :" + this.trace + " . "
							+ e.getMessage());
					Map<String, String> renderMap = makeHtml(id, "Fehler");
					res.add(renderMap);
				}
			}
			Map<String, String> renderMap = makeHtml(id, groovy.get(this.trace)
					.toString());
			res.add(renderMap);
		}
		ArrayList<String> traces = getTraceList();
		String traceList = WebUtils.toJson(getTraceList());
		Map<String, String> traceDropdownMap = WebUtils.wrap("cmd",
				"Worksheet.setDropdown", "id", id, "dropdownName",
				"trace-selection", "items", traceList);
		res.add(traceDropdownMap);
		return res;
	}

	@Override
	protected String getTemplate() {
		return "/ui/worksheet/boxes/trace.html";
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
		if (this.content.equals(""))
			this.content = null;
		this.trace = data.get("additionalData")[0];
		if (this.trace.equals(""))
			this.trace = null;
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}

	private ArrayList<String> getTraceList() {
		ScriptEngine groovy = owner.getGroovy();
		Set<Entry<String, Object>> engineBindings = groovy.getBindings(
				ScriptContext.ENGINE_SCOPE).entrySet();
		ArrayList<String> traceKeys = new ArrayList<String>();
		Iterator<Entry<String, Object>> it = engineBindings.iterator();
		while (it.hasNext()) {
			Entry<String, Object> next = it.next();
			if (next.getValue() instanceof de.prob.statespace.Trace)
				traceKeys.add(next.getKey());
		}
		Collections.sort(traceKeys);
		if (this.trace != null) {
			traceKeys.remove(this.trace);
			traceKeys.add(0, this.trace);
		}
		return traceKeys;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends Object> additionalMessages() {
		String traceList = WebUtils.toJson(getTraceList());
		Map<String, String> traceDropdownMap = WebUtils.wrap("cmd",
				"Worksheet.setDropdown", "id", id, "dropdownName",
				"trace-selection", "items", traceList);
		return pack(traceDropdownMap);
	}
}
