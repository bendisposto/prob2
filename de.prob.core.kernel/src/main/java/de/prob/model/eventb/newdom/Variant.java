package de.prob.model.eventb.newdom;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.newdom.AbstractElement;

public class Variant extends AbstractElement {
	private final IEvalElement expression;

	public Variant(final String code) {
		expression = new EventB(code);
	}

	public IEvalElement getExpression() {
		return expression;
	}
}
