package de.prob.model.classicalb;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.Variable;

public class ClassicalBVariable extends Variable {

	private final String name;

	public ClassicalBVariable(final Start start) {
		super(new ClassicalB(start));
		this.name = expression.getCode();
	}

	public String getName() {
		return name;
	}

}
