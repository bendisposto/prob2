package de.prob.model.eventb.proof;

import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBInvariant;

public class INV implements IProof {

	public final Event event;
	public final EventBInvariant invariant;
	private final String proofName;
	private final ProofState proofState;
	private final boolean discharged;

	public INV(final String proofName, final Event event,
			final EventBInvariant invariant, final boolean discharged,
			final ProofState proofState) {
		this.proofName = proofName;
		this.event = event;
		this.invariant = invariant;
		this.discharged = discharged;
		this.proofState = proofState;
	}

	@Override
	public String getProofName() {
		return proofName;
	}

	@Override
	public boolean isDischarged() {
		return discharged;
	}

	@Override
	public ProofState getProofState() {
		return proofState;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
