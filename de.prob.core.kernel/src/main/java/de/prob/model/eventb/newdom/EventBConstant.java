package de.prob.model.eventb.newdom;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.newdom.Constant;

public class EventBConstant extends Constant {

	private final String name;

	public EventBConstant(final String name) {
		super(new EventB(name));
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
