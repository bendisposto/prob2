package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.model.representation.Constant;

public class EventBConstant extends Constant {

	private final String name;
	private final boolean isAbstract;
	private final String unit;
	private final String comment;

	public EventBConstant(final String name, final boolean isAbstract,
			final String unit) {
		this(name, isAbstract, unit, "");
	}

	public EventBConstant(String name, final boolean isAbstract,
			final String unit, String comment) {
		super(new EventB(name, FormulaExpand.EXPAND));
		this.name = name;
		this.isAbstract = isAbstract;
		this.unit = unit;
		this.comment = comment == null ? "" : comment;
	}

	@Override
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

	public String getComment() {
		return comment;
	}
}
