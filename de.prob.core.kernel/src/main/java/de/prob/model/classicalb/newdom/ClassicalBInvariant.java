package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Invariant;

public class ClassicalBInvariant extends Invariant {

	public ClassicalBInvariant(final String code) throws BException {
		super(new ClassicalB(code));
	}

}
