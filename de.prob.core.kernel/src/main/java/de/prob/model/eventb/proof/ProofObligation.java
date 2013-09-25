package de.prob.model.eventb.proof;

import java.util.List;
import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.translate.ProofTreeCreator;
import de.prob.prolog.output.IPrologTermOutput;

public abstract class ProofObligation extends SimpleProofNode {

	String name;
	private final ProofTreeCreator creator;

	public ProofObligation(final String name, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(goal, null, discharged, description);
		this.name = name;
		this.creator = creator;
	}

	public String getName() {
		return name;
	}

	@Override
	public Set<EventB> getHypotheses() {
		if (hypotheses == null) {
			hypotheses = creator.getHyps();
		}
		return super.getHypotheses();
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

	@Override
	public Set<SimpleProofNode> getChildrenNodes() {
		if (!creator.isCreated()) {
			List<SimpleProofNode> kids = creator.create();
			addChildrenNodes(kids);
		}
		return childrenStates;
	}

}