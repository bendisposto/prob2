package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class Selection {
	private final String selector;
	private final List<Attribute> attributes;

	public Selection(final String selector) {
		this.selector = selector;
		attributes = new ArrayList<Attribute>();
	}

	public Selection(final String selector, final List<Attribute> attributes) {
		this.selector = selector;
		this.attributes = attributes;
	}

	public Selection attr(final String name, final String attr) {
		List<Attribute> attrs = new ArrayList<Selection.Attribute>(attributes);
		attrs.add(new Attribute(name, attr));
		return new Selection(selector, attrs);
	}

	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

	class Attribute {
		private final String name;
		private final String attr;

		public Attribute(final String name, final String attr) {
			this.name = name;
			this.attr = attr;
		}
	}
}
