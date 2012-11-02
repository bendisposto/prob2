package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Constant;

public class ClassicalBConstant extends Constant {

	private final String name;

	public ClassicalBConstant(final Start start) {
		super(new ClassicalB(start));
		this.name = expression.getCode();
	}

	public String getName() {
		return name;
	}

}
