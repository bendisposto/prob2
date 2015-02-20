package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class EventBVariable extends Variable {

	private final String name;
	private final String unit;

	public EventBVariable(final String name, final String unit) {
		super(new EventB(name));
		this.name = name;
		this.unit = unit;
	}

	public boolean hasUnit() {
		return unit != null;
	}

	public String getUnit() {
		return unit;
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
		if (that instanceof EventBVariable
				&& getExpression().getCode().equals(
						((EventBVariable) that).getExpression().getCode())) {
			return true;
		}
		return super.equals(that);
	}

	@Override
	public int hashCode() {
		return getExpression().getCode().hashCode();
	}
}
