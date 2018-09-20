package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;
import de.prob.model.representation.Named;

public class EventParameter extends AbstractFormulaElement implements Named {

	private final String name;
	private final EventB expression;
	private final String comment;

	public EventParameter(final String name) {
		this(name, "");
	}

	public EventParameter(final String name, String comment) {
		this.name = name;
		this.comment = comment == null ? "" : comment;
		expression = new EventB(name, FormulaExpand.EXPAND);
	}

	@Override
	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public String toString() {
		return name;
	}

	public EventB getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getExpression();
	}

	@Override
	public boolean equals(final Object that) {
		return that == this || (
			that instanceof EventParameter
			&& getExpression().getCode().equals(((EventParameter)that).getExpression().getCode())
		);
	}

	@Override
	public int hashCode() {
		return getExpression().getCode().hashCode();
	}

}
