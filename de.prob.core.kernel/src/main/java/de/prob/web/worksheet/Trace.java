package de.prob.web.worksheet;

import java.util.List;
import java.util.Map;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class Trace extends AbstractBox {
	private String content = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> render(BindingsSnapshot snapshot) {
		ScriptEngine groovy = owner.getGroovy();
		groovy.getBindings(ScriptContext.ENGINE_SCOPE);
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
}
