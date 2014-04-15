package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.StateSpace;

public abstract class AbstractFormulaElement extends AbstractElement {

	abstract public IEvalElement getFormula();

	public void subscribe(final StateSpace s) {
		s.subscribe(this, getFormula());
	}

	public void unsubscribe(final StateSpace s) {
		s.unsubscribe(this, getFormula());
	}

	public void isSubscribed(final StateSpace s) {
		s.isSubscribed(getFormula());
	}
}
