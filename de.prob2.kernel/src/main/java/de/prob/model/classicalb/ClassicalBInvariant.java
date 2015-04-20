package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Invariant;

public class ClassicalBInvariant extends Invariant {

	public ClassicalBInvariant(final Start start) {
		super(new ClassicalB(start));
	}

	@Override
	public boolean isTheorem() {
		return false;
	}

}
