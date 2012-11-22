package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public abstract class Constant extends AbstractElement implements IEval {

	protected final IEvalElement expression;

	public Constant(final IEvalElement expression) {
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getEvaluate() {
		return expression;
	}
	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(expression.getCode());
	}
	
}
