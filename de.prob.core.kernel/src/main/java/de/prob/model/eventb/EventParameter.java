package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.IEval;

public class EventParameter extends AbstractElement implements IEval {

	private final String name;
	private final EventB expression;

	public EventParameter(final String name) {
		this.name = name;
		expression = new EventB(name);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public EventB getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getEvaluate() {
		return expression;
	}

}
