package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Guard;

public class ClassicalBGuard extends Guard {

	public ClassicalBGuard(final Start guard) {
		super(new ClassicalB(guard));
	}

}
