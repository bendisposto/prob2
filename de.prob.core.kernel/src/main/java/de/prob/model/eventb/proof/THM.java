package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class THM extends SimpleProofNode {

	private final AbstractElement target;
	private final String name;

	public THM(final String proofName, final AbstractElement target,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.target = target;
	}

	public String getName() {
		return name;
	}

	public AbstractElement getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return name;
	}
}
