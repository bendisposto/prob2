package de.prob.model.representation.newdom;

public abstract class BEvent extends AbstractElement {

	private final String name;

	public BEvent(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
