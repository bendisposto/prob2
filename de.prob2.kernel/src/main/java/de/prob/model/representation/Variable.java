package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public abstract class Variable extends AbstractFormulaElement implements Named {

	protected final IEvalElement expression;

	public Variable(final IEvalElement expression) {
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
	public String getName() {
		return expression.getCode();
	}

	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(expression.getCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Variable) {
			return expression.equals(((Variable) obj).getExpression());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}
}
