package de.prob.model.representation.newdom;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Axiom extends AbstractElement {

	private final IEvalElement predicate;

	public Axiom(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}
}
