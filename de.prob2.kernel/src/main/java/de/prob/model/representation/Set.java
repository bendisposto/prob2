package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public class Set extends AbstractFormulaElement implements Named {

	private final IEvalElement formula;
	private final String comment;

	public Set(final IEvalElement formula) {
		this(formula, "");
	}

	public Set(final IEvalElement formula, String comment) {
		this.formula = formula;
		this.comment = comment == null ? "" : comment;
	}

	@Override
	public String getName() {
		return formula.getCode();
	}

	public String getComment() {
		return comment;
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
