package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;

public class EventParameter extends AbstractElement {

	private final String name;
	private final EventB expression;

	public EventParameter(final String name) {
		this.name = name;
		expression = new EventB(name);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public EventB getExpression() {
		return expression;
	}

	public IEvalElement getFormula() {
		return expression;
	}

	@Override
	public boolean equals(final Object that) {
		if (that == this) {
			return true;
		}
		if (that instanceof EventParameter
				&& getExpression().getCode().equals(
						((EventParameter) that).getExpression().getCode())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getExpression().getCode().hashCode();
	}

}
