package de.prob.model.eventb;

import de.prob.model.representation.AbstractElement;

public class EventParameter extends AbstractElement {

	private final String name;

	public EventParameter(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
