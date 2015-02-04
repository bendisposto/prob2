package de.prob.animator.domainobjects;

import java.util.List;

import de.prob.util.StringUtil;

public class DotNode {

	private final String id;
	private final List<String> labels;
	private int count;
	private final String color;
		
	public DotNode(final String id, final List<String> labels,
			final int count, String color) {
		this.id = id;		
		this.labels = labels;
		this.count = count;
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public List<String> getLabels() {
		return labels;
	}

	public int getCount() {
		return count;
	}

	public String getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return StringUtil.generateJsonString(this);
	}
	
}
