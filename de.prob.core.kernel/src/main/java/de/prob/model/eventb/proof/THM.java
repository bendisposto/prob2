package de.prob.model.eventb.proof;

import de.prob.model.representation.AbstractElement;

public class THM implements IProof {

	public final AbstractElement target;
	private final String proofName;
	private final ProofState proofState;
	private final boolean discharged;

	public THM(final String proofName, final AbstractElement target,
			final boolean discharged, final ProofState proofState) {
		this.proofName = proofName;
		this.target = target;
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
