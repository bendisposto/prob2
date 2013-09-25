package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Variant;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class FIN extends ProofObligation {

	private final Variant variant;

	public FIN(final String name, final Variant variant, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(name, goal, discharged, description, creator);
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
