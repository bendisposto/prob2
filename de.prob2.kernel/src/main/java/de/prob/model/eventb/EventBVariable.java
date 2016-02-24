package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.Variable;

public class EventBVariable extends Variable {

	private final String name;
	private final String unit;
	private final String comment;

	public EventBVariable(final String name, final String unit) {
		this(name, unit, "");
	}

	public EventBVariable(final String name, final String unit, String comment) {
		super(new EventB(name));
		this.name = name;
		this.unit = unit;
		this.comment = comment == null ? "" : comment;
	}

	public boolean hasUnit() {
		return unit != null;
	}

	public String getUnit() {
		return unit;
	}

	public String getComment() {
		return comment;
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
