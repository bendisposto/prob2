package de.prob.model.eventb.newdom;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.newdom.AbstractElement;

public class Variant extends AbstractElement {
	private final IEvalElement predicate;

	public Variant(final String code) {
		predicate = new EventB(code);
	}

	public IEvalElement getPredicate() {
		return predicate;
	}
}
