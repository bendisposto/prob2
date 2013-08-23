package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class OperatorArgument extends AbstractElement {

	private final EventB identifier;
	private final EventB expression;

	public OperatorArgument(final String identifier, final String expression) {
		this.identifier = new EventB(identifier);
		this.expression = new EventB(expression);
	}

	public EventB getIdentifier() {
		return identifier;
	}

	public EventB getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		return identifier.getCode() + " : " + expression.getCode();
	}
}
