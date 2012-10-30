package de.prob.model.representation.newdom;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Invariant extends AbstractElement {

	private IEvalElement predicate;

	public Invariant(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}
}
