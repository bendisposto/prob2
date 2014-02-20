package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Witness;
import de.prob.prolog.output.IPrologTermOutput;

public class WWD extends CalculatedPO {

	private final Witness witness;

	public WWD(final String sourceName, final String name,
			final Witness witness, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(sourceName, name, discharged, description, goal, hypotheses);
		this.witness = witness;
	}

	public Witness getWitness() {
		return witness;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
	}

}
