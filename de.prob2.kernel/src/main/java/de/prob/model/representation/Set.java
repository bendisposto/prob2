package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public class Set extends AbstractFormulaElement {

	private final IEvalElement formula;

	public Set(final IEvalElement formula) {
		this.formula = formula;

	}

	public String getName() {
		return formula.getCode();
	}

	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(getName());
	}

	@Override
	public IEvalElement getFormula() {
		return formula;
	}
}
