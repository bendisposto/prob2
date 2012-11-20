package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Invariant extends AbstractElement implements IEval {

	private final IEvalElement predicate;

	public Invariant(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getEvaluate() {
		return predicate;
	}
}
