package de.prob.animator.domainobjects;

import de.prob.util.StringUtil;


public class DotEdge {

	public final String id;
	public final String src;
	public final String dest;
	public final String label;
	public final String style;
	public final String color;
	
	public DotEdge(final String id, final String src, final String dest,
			final String label, String style, String color) {
		this.id = id;
		this.src = src;
		this.dest = dest;
		this.label = label;
		this.style = style;
		this.color = color;
	}	

	public String getId() {
		return id;
	}

	public String getSrc() {
		return src;
	}

	public String getDest() {
		return dest;
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
		return StringUtil.generateJsonString(this);
	}
	
}
