package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractTheoremElement;
import de.prob.unicode.UnicodeTranslator;

public class Assertion extends AbstractTheoremElement {

	private final ClassicalB predicate;

	public Assertion(final Start start) {
		predicate = new ClassicalB(start);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getPredicate();
	}

	@Override
	public boolean isTheorem() {
		return true;
	}
	
	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(predicate.getCode());
	}
}
