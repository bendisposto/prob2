package de.prob.model;

public class StringWithLocation {

	public final long location;
	public final String content;

	public StringWithLocation(final String content, final long location) {
		this.content = content;
		this.location = location;
	}

	@Override
	public String toString() {
		return content.toString();
	}
}
