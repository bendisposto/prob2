package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.AbstractElement;

public class Assertion extends AbstractElement {

	private final ClassicalB predicate;

	public Assertion(final String code) throws BException {
		predicate = new ClassicalB(code);
	}

	public ClassicalB getPredicate() {
		return predicate;
	}
}
