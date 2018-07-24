package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public abstract class Invariant extends AbstractTheoremElement {

	private final IEvalElement predicate;

	public Invariant(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getPredicate();
	}

	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(predicate.getCode());
	}

}
