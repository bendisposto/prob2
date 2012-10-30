package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Variable;

public class ClassicalBVariable extends Variable {

	private final String name;

	public ClassicalBVariable(final String name) throws BException {
		super(new ClassicalB(name));
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
