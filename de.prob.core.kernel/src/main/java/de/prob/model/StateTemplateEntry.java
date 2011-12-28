package de.prob.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class StateTemplateEntry {
	private List<StateTemplateEntry> children = new ArrayList<StateTemplateEntry>();

	public void addChild(final StateTemplateEntry child) {
		children.add(child);
	}

	public List<StateTemplateEntry> getChildren() {
		return children;
	}

	public abstract String prettyPrint(Map<String, String> m);
}
