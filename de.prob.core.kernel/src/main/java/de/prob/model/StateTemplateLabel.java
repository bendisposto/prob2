package de.prob.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;

public class StateTemplateLabel extends StateTemplateEntry {
	private String text;

	public StateTemplateLabel(final String text) {
		this.text = text;
	}

	@Override
	public String prettyPrint(final Map<String, String> m) {
		List<StateTemplateEntry> list = getChildren();
		ArrayList<String> c = new ArrayList<String>();
		for (StateTemplateEntry e : list) {
			String prettyPrint = e.prettyPrint(m);
			c.add(prettyPrint);
		}
		return text + "\n" + Joiner.on(", ").join(c);
	}
}
