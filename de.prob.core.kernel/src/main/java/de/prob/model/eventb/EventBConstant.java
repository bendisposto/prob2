package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Constant;

public class EventBConstant extends Constant {

	private final String name;
	private final boolean isAbstract;

	public EventBConstant(final String name, final boolean isAbstract) {
		super(new EventB(name));
		this.name = name;
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}
}
