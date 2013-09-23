package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.prolog.output.IPrologTermOutput;

public abstract class ProofObligation extends SimpleProofNode {

	String name;

	public ProofObligation(final String name, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * This method writes the source elements contained in a Proof Obligation in
	 * the given {@link IPrologTermOutput}. If certain elements are needed for a
	 * given proof obligation, then this proof obligation must override this
	 * method.
	 * 
	 * @param pto
	 */
	public void toProlog(final IPrologTermOutput pto) {

	}

}