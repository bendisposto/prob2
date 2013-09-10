package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Variant;

public class VWD extends SimpleProofNode {

	private final String name;
	private final Variant variant;

	public VWD(final String name, final Variant variant, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		this.name = name;
		this.variant = variant;
	}

	public String getName() {
		return name;
	}

	public Variant getVariant() {
		return variant;
	}

	@Override
	public String toString() {
		return name;
	}

}
