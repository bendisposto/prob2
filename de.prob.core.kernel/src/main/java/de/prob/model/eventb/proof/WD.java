package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class WD extends SimpleProofNode {

	private final AbstractElement target;
	private final String proofName;

	public WD(final String proofName, final AbstractElement target,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		this.proofName = proofName;
		this.target = target;
	}

	public String getName() {
		return proofName;
	}

	public AbstractElement getTarget() {
		return target;
	}

}
