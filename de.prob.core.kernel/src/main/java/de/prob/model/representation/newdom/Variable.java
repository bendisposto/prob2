package de.prob.model.representation.newdom;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Variable extends AbstractElement {

	private final IEvalElement expression;

	public Variable(final IEvalElement expression) {
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}
}
