package de.prob.visualization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;

/**
 * @deprecated This class will be removed unless you make a strong case for it!
 */
@Deprecated
public class Transformer {
	public static final List<String> DEFAULT_STYLES = Collections.unmodifiableList(
			Arrays.asList("fill", "font", "stroke", "stroke-dasharray", "stroke-width"));

	public final List<Attribute> attributes;
	public final List<Style> styles;

	public Transformer(final String selector) {
		attributes = new ArrayList<>();
		styles = new ArrayList<>();
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
		if (Transformer.DEFAULT_STYLES.contains(name))
			return style(name, value);
		return attr(name, value);
	}

	@Override
	public String toString() {
		Gson g = new Gson();
		return g.toJson(this);
	}

	class Attribute {
		public final String name;
		public final String value;

		public Attribute(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
	}

	class Style {
		public final String name;
		public final String value;

		public Style(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public List<Style> getStyles() {
		return styles;
	}
}
