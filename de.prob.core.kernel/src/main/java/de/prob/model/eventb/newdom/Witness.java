package de.prob.model.eventb.newdom;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.newdom.AbstractElement;

public class Witness extends AbstractElement {

	private final String name;
	private final EventB predicate;

	public Witness(final String name, final String code) {
		this.name = name;
		predicate = new EventB(code);
	}

	public String getName() {
		return name;
	}

	public EventB getPredicate() {
		return predicate;
	}
}
