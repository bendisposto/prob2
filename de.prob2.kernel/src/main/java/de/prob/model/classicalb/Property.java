package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Axiom;

public class Property extends Axiom {

	public Property(final Start start) {
		super(new ClassicalB(start));
	}

	@Override
	public boolean isTheorem() {
		// TODO: is this true?
		return false;
	}

}
