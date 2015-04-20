package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Action;

public class ClassicalBAction extends Action {

	public ClassicalBAction(final Start code) {
		super(new ClassicalB(code));
	}

}
