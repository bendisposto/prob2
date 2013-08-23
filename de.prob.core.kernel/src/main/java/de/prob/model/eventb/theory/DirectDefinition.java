package de.prob.model.eventb.theory;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.IEval;

public class DirectDefinition extends AbstractElement implements
		IOperatorDefinition, IEval {

	EventB formula;

	public DirectDefinition(final String formula) {
		this.formula = new EventB(formula);
	}

	public EventB getFormula() {
		return formula;
	}

	@Override
	public IEvalElement getEvaluate() {
		return formula;
	}

	@Override
	public String toString() {
		return formula.toString();
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}
}
