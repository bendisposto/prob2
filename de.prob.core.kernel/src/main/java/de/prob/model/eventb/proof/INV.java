package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.translate.ProofTreeCreator;
import de.prob.prolog.output.IPrologTermOutput;

public class INV extends ProofObligation {

	private final Event event;
	private final EventBInvariant invariant;

	public INV(final String proofName, final Event event,
			final EventBInvariant invariant, final EventB goal,
			final boolean discharged, final String description,
			final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
		this.event = event;
		this.invariant = invariant;
	}

	public EventBInvariant getInvariant() {
		return invariant;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void toProlog(final IPrologTermOutput pto) {
		pto.openTerm("event");
		pto.printAtom(event.getName());
		pto.closeTerm();

		pto.openTerm("invariant");
		pto.printAtom(invariant.getName());
		pto.closeTerm();
	}
}