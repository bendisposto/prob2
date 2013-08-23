package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;

public class InferenceRule extends AbstractElement {

	private final List<IEvalElement> given;
	private final IEvalElement infer;

	public InferenceRule(final List<IEvalElement> given,
			final IEvalElement infer) {
		this.given = given;
		this.infer = infer;
	}

	public List<IEvalElement> getGiven() {
		return given;
	}

	public IEvalElement getInfer() {
		return infer;
	}

	@Override
	public String toString() {
		return given.toString() + " => " + infer.toString();
	}
}
