package de.prob.model.eventb.theory;

import java.util.List;

import com.google.common.base.Objects;

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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InferenceRule other = (InferenceRule) obj;
		if (given == null) {
			if (other.given != null) {
				return false;
			}
		} else if (!given.equals(other.given)) {
			return false;
		}
		if (infer == null) {
			if (other.infer != null) {
				return false;
			}
		} else if (!infer.equals(other.infer)) {
			return false;
		}
		return Objects.equal(given, other.getGiven())
				&& Objects.equal(infer, other.getInfer());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(given, infer);
	}
}
