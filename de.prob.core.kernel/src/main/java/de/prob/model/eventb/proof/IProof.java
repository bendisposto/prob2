package de.prob.model.eventb.proof;

public interface IProof {

	public String getProofName();

	public ProofState getProofState();

	public boolean isDischarged();

	public String getDescription();
}
