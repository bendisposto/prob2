package de.prob.model.eventb.theory;

import java.util.List;
import java.util.Objects;

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
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final InferenceRule other = (InferenceRule)obj;
		return Objects.equals(this.getGiven(), other.getGiven())
				&& Objects.equals(this.getInfer(), other.getInfer());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getGiven(), this.getInfer());
	}
}
