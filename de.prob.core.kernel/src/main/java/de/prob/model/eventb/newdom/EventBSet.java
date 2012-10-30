package de.prob.model.eventb.newdom;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.newdom.Set;

public class EventBSet extends Set {

	private final String name;

	public EventBSet(final String name) {
		super(new EventB(name));
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
