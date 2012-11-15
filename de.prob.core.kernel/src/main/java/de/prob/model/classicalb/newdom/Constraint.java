package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.newdom.AbstractElement;
import de.prob.model.representation.newdom.IEval;

public class Constraint extends AbstractElement implements IEval {

	private final ClassicalB predicate;

	public Constraint(final Start ast) {
		predicate = new ClassicalB(ast);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getEvaluate() {
		return predicate;
	}
}
