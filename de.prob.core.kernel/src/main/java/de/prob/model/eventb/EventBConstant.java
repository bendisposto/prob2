package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Constant;

public class EventBConstant extends Constant {

	private final String name;
	private final boolean isAbstract;
	private final String unit;

	public EventBConstant(final String name, final boolean isAbstract,
			final String unit) {
		super(new EventB(name));
		this.name = name;
		this.isAbstract = isAbstract;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean hasUnit() {
		return unit != null;
	}

	public String getUnit() {
		return unit;
	}
}
