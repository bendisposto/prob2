package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;

public class THM extends SimpleProofNode {

	private final AbstractElement originalFormula;
	private final String name;

	public THM(final String proofName, final AbstractElement originalFormula,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.originalFormula = originalFormula;
	}

	public String getName() {
		return name;
	}

	public AbstractElement getOriginalFormula() {
		return originalFormula;
	}

	@Override
	public String toString() {
		return name;
	}
}
