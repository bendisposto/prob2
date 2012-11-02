package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.AbstractElement;

public class Assertion extends AbstractElement {

	private final ClassicalB predicate;

	public Assertion(final Start start) {
		predicate = new ClassicalB(start);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}
}
