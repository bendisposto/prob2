package de.prob.model.representation.newdom;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Guard extends AbstractElement {

	private final IEvalElement predicate;

	public Guard(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}
}
