package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Witness;
import de.prob.model.eventb.translate.ProofTreeCreator;

public class WFIS extends ProofObligation {

	private final Witness witness;

	public WFIS(final String name, final Witness witness, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(name, goal, discharged, description, creator);
		this.witness = witness;
	}

	public Witness getWitness() {
		return witness;
	}

	@Override
	public String toString() {
		return name;
	}

}
