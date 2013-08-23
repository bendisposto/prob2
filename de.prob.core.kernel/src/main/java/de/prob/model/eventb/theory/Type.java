package de.prob.model.eventb.theory;

import de.prob.model.representation.AbstractElement;

public class Type extends AbstractElement {

	private final String name;

	public Type(final String identifier) {
		name = identifier;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
