package de.prob.model.representation.newdom;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Constant extends AbstractElement {

	protected final IEvalElement expression;

	public Constant(final IEvalElement expression) {
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}
}
