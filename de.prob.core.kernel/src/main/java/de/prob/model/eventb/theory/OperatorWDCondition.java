package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.IEval;

public class OperatorWDCondition extends AbstractElement implements IEval {

	private final EventB predicate;

	public OperatorWDCondition(final String predicate) {
		this.predicate = new EventB(predicate);
	}

	@Override
	public IEvalElement getEvaluate() {
		return predicate;
	}

	public EventB getPredicate() {
		return predicate;
	}

	@Override
	public String toString() {
		return predicate.toString();
	}
}
