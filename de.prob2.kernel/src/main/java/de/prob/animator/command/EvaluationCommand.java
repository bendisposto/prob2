package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvalResult;

public abstract class EvaluationCommand extends AbstractCommand {

	protected IEvalResult value;
	protected final String stateId;
	protected final IEvalElement evalElement;

	public EvaluationCommand(final IEvalElement evalElement, final String id) {
		this.evalElement = evalElement;
		this.stateId = id;
	}

	public IEvalElement getEvalElement() {
		return evalElement;
	}

	public IEvalResult getValue() {
		return value;
	}

	public String getStateId() {
		return stateId;
	}
}
