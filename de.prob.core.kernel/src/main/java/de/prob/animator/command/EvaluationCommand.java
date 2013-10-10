package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.IEvaluationResult;


public abstract class EvaluationCommand extends AbstractCommand {

	/*
	 * TODO: we shouldn't need a list here as we only hold one element, worry
	 * about this later
	 */
	protected final List<IEvaluationResult> values = new ArrayList<IEvaluationResult>();
	protected final List<IEvalElement> evalElements;
	protected final String stateId;

	public EvaluationCommand(List<IEvalElement> evalElements, String id) {
		this.evalElements = evalElements;
		this.stateId = id;
	}

	public List<IEvaluationResult> getValues() {
		return values;
	}

	public List<IEvalElement> getFormulas() {
		return evalElements;
	}

	public String getStateId() {
		return stateId;
	}
}
