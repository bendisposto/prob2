package de.prob.model.representation.newdom;

public abstract class Set extends AbstractElement {

	private final String name;

	public Set(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
