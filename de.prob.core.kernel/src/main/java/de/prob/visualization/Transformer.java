package de.prob.visualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class Transformer {
	public static final List<String> STYLES = Arrays.asList(new String[] {
			"fill", "font", "stroke", "stroke-dasharray" });

	public String selector;
	public final List<Attribute> attributes;
	public final List<Style> styles;

	public Transformer(final String selector) {
		this.selector = selector;
		attributes = new ArrayList<Attribute>();
		styles = new ArrayList<Style>();
	}

	private Transformer attr(final String name, final String value) {
		attributes.add(new Attribute(name, value));
		return this;
	}

	private Transformer style(final String name, final String value) {
		styles.add(new Style(name, value));
		return this;
	}

	public Transformer set(final String name, final String value) {
		if (Transformer.STYLES.contains(name)) {
			return style(name, value);
		}
		return attr(name, value);
	}

	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

	class Attribute {
		private final String name;
		private final String value;

		public Attribute(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
	}

	class Style {
		private final String name;
		private final String value;

		public Style(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
	}

	public Transformer updateSelector(final String selector) {
		this.selector = selector;
		return this;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public List<Style> getStyles() {
		return styles;
	}
}
