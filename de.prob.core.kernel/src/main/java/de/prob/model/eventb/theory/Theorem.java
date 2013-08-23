package de.prob.model.eventb.theory;

import de.prob.model.representation.AbstractElement;

public class Theorem extends AbstractElement {

	private final String name;
	private final String predicate;

	public Theorem(final String name, final String predicate) {
		this.name = name;
		this.predicate = predicate;
	}

	public String getName() {
		return name;
	}

	public String getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return name;
	}

}
