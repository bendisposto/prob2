package de.prob.web.views;

import java.util.HashMap;
import java.util.Map;

import de.prob.web.WebUtils;

class Box {

	public final String id;
	public final String type;
	public final boolean codemirror;
	public final String template;
	private String content = "";
	private final Map<String, String> basemap;

	public Box(int id, String type, String template, boolean codemirror,
			Map<String, String> extras) {
		this(String.valueOf(id), type, template, codemirror, extras);
	}

	public Box(String id, String type, String template, boolean codemirror,
			Map<String, String> extras) {
		this.id = id;
		this.type = type;
		this.template = template;
		this.codemirror = codemirror;
		this.basemap = new HashMap<String, String>();
		basemap.putAll(extras);
	}

	private Map<String, String> create(String cmd) {
		Map<String, String> m = new HashMap<String, String>();
		m.putAll(this.basemap);
		m.putAll(WebUtils.wrap("number", id, "type", type, "content",
				getContent(), "renderedhtml", "", "template", template,
				"codemirror", codemirror));
		m.put("cmd", "Worksheet." + cmd);
		return m;
	}

	public Map<String, String> createMessage() {
		return create("render_box");
	}

	public Map<String, String> replaceMessage() {
		return create("replace_box");
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}