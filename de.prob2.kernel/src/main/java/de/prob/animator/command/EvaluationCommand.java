package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.AbstractEvalResult;

public abstract class EvaluationCommand extends AbstractCommand {

	protected AbstractEvalResult value;
	protected final String stateId;
	protected final IEvalElement evalElement;

	public EvaluationCommand(final IEvalElement evalElement, final String id) {
		this.evalElement = evalElement;
		this.stateId = id;
	}

	public IEvalElement getEvalElement() {
		return evalElement;
	}

	public AbstractEvalResult getValue() {
		return value;
	}

	public String getStateId() {
		return stateId;
	}
}
