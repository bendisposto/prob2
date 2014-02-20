package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Variant;
import de.prob.prolog.output.IPrologTermOutput;

public class FIN extends CalculatedPO {

	private final Variant variant;

	public FIN(final String sourceName, final String name,
			final Variant variant, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(sourceName, name, discharged, description, goal, hypotheses);
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
	}

}
