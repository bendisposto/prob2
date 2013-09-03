package de.prob.model.classicalb;

import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Action;

public class ClassicalBAction extends Action {

	public ClassicalBAction(final String code) {
		super(new ClassicalB(code));
	}

}
