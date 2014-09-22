package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

public class Assertion extends AbstractFormulaElement {

	private final ClassicalB predicate;

	public Assertion(final Start start) {
		predicate = new ClassicalB(start);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getFormula() {
		return predicate;
	}
}
