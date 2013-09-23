package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Variant;

public class VWD extends ProofObligation {

	private final Variant variant;

	public VWD(final String name, final Variant variant, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(name, goal, hypotheses, discharged, description);
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}

	@Override
	public String toString() {
		return name;
	}

}
