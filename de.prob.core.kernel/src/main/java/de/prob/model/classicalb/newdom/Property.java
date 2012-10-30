package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Axiom;

public class Property extends Axiom {

	public Property(final String code) throws BException {
		super(new ClassicalB(code));
	}

}
