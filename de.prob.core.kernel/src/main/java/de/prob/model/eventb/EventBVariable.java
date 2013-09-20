package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class EventBVariable extends Variable {

	private final String name;

	public EventBVariable(final String name) {
		super(new EventB(name));
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(final Object that) {
		if (this == that) {
			return true;
		}
		if (this instanceof EventBVariable
				&& this.getExpression().equals(
						((EventBVariable) that).getExpression())) {
			return true;
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		return getExpression().hashCode();
	}
}
