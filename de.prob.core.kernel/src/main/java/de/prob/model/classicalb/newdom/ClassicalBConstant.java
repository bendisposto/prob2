package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Constant;

public class ClassicalBConstant extends Constant {

	private final String name;

	public ClassicalBConstant(final String name) throws BException {
		super(new ClassicalB(name));
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
