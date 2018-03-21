package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractFormulaElement;

public class Constraint extends AbstractFormulaElement {

	private final ClassicalB predicate;

	public Constraint(final Start ast) {
		predicate = new ClassicalB(ast);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getFormula() {
		return this.getPredicate();
	}
}
