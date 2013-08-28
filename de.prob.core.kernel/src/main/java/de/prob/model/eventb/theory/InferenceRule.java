package de.prob.model.eventb.theory;

import java.util.List;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class InferenceRule extends AbstractElement {

	private final List<EventB> given;
	private final EventB infer;

	public InferenceRule(final List<EventB> given, final EventB infer) {
		this.given = given;
		this.infer = infer;
	}

	public List<EventB> getGiven() {
		return given;
	}

	public EventB getInfer() {
		return infer;
	}

	@Override
	public String toString() {
		return given.toString() + " => " + infer.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof InferenceRule) {
			return given.equals(((InferenceRule) obj).getGiven())
					&& infer.equals(((InferenceRule) obj).getInfer());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 13 * given.hashCode() + 17 * infer.hashCode();
	}
}
