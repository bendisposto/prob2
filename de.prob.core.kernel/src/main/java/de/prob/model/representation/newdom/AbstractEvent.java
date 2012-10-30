package de.prob.model.representation.newdom;

public abstract class AbstractEvent extends AbstractElement {

	private final String name;

	public AbstractEvent(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
