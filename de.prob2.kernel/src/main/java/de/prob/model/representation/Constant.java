package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.Trace;
import de.prob.unicode.UnicodeTranslator;

public abstract class Constant extends AbstractFormulaElement {

	protected final IEvalElement expression;
	protected IEvalResult result;

	public Constant(final IEvalElement expression) {
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getFormula() {
		return expression;
	}

	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(expression.getCode());
	}

	// Experimental. Would allow the user to calculate the value once and cache
	// it.
	public IEvalResult getValue(final Trace h) {
		if (result == null) {
			result = h.evalCurrent(getFormula());
		}
		return result;
	}

}
