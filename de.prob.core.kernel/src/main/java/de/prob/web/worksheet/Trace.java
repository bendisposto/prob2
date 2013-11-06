package de.prob.web.worksheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

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
		Set<Entry<String, Object>> traces = Sets.filter(engineBindings,
				new Predicate<Entry<String, Object>>() {
					@Override
					public boolean apply(Entry<String, Object> input) {
						return input.getValue() instanceof Trace;
					}
				});
		Map<String, Object> editorArgs = new HashMap<String, Object>();
		editorArgs.put("traces", traces);
		Map<String, String> map = new HashMap<String, String>();
		map.put("editorArgs", WebUtils.toJson(editorArgs));
		return map;
	}

}
