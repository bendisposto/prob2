package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;

public abstract class Action extends AbstractElement {

	private final IEvalElement code;

	public Action(final IEvalElement code) {
		this.code = code;
	}

	public IEvalElement getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code.toString();
	}
}
