package de.prob.model;

import java.util.Map;

public class StateTemplateValue extends StateTemplateEntry {
	private String defaultValue;
	private String name;

	public StateTemplateValue(final String name, final String defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String prettyPrint(final Map<String, String> m) {
		String val = m.get(name);
		return name + "=" + (val == null ? defaultValue : val);
	}

}
