package de.prob.model.classicalb.newdom;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.domainobjects.ClassicalB;
import de.prob.model.representation.newdom.Axiom;

public class Property extends Axiom {

	public Property(final Start start) {
		super(new ClassicalB(start));
	}

}
