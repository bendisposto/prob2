package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.AbstractElement;

public class Constraint extends AbstractElement {

	private final ClassicalB predicate;

	public Constraint(final Start ast) {
		predicate = new ClassicalB(ast);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}

}
