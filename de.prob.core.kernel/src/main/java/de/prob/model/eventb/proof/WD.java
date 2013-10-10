package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public class WD extends SimpleProofNode implements IProofObligation {

	private final AbstractElement originalFormula;
	private final String name;

	public WD(final String proofName, final AbstractElement originalFormula,
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

	@Override
	public void toProlog(final IPrologTermOutput pto) {
		// TODO Auto-generated method stub

	}
}
