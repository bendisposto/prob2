package de.prob.web.worksheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import de.prob.web.WebUtils;

public class Trace extends AbstractBox {
	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		Map<String, String> map = makeHtml(id, "Kommt noch");
		return pack(map);
	}

	@Override
	protected String getTemplate() {
		return "/ui/worksheet/boxes/trace.html";
	}

	@Override
	public void setContent(Map<String, String[]> data) {
		this.content = data.get("text")[0];
	}

	@Override
	protected String getContentAsJson() {
		return content;
	}

	@Override
	public EChangeEffect changeEffect() {
		return EChangeEffect.EVERYTHING_BELOW;
	}

	@Override
	protected Map<String, String> getAdditionalEntries() {
		ScriptEngine groovy = owner.getGroovy();
		Set<Entry<String, Object>> engineBindings = groovy.getBindings(
				ScriptContext.ENGINE_SCOPE).entrySet();
		Map<String, Object> editorArgs = new HashMap<String, Object>();
		ArrayList<String> traceKeys = new ArrayList<String>();
		Iterator<Entry<String, Object>> it = engineBindings.iterator();
		while (it.hasNext()) {
			Entry<String, Object> next = it.next();
			if (next.getValue() instanceof de.prob.statespace.Trace)
				traceKeys.add(next.getKey());
		}
		editorArgs.put("traces", traceKeys);
		Map<String, String> map = new HashMap<String, String>();
		map.put("editorArgs", WebUtils.toJson(editorArgs));
		return map;
	}

}
