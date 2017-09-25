package de.prob.animator.domainobjects;

import com.google.common.base.MoreObjects;

public class DotEdge {

	public final String id;
	public final String source;
	public final String target;
	public final String label;
	public final String style;
	public final String color;
	
	public DotEdge(final String id, final String source, final String target,
			final String label, String style, String color) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.label = label;
		this.style = style;
		this.color = color;
	}	

	public String getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public String getLabel() {
		return label;
	}

	public String getStyle() {
		return style;
	}

	public String getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id)
			.add("source", source)
			.add("target", target)
			.add("label", label)
			.add("style", style)
			.add("color", color)
			.toString();
	}
	
}
