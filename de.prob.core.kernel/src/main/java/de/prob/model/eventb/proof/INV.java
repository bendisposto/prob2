package de.prob.model.eventb.proof;

import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBInvariant;

public class INV implements IProof {

	public final Event event;
	public final EventBInvariant invariant;

	public INV(final Event event, final EventBInvariant invariant) {
		this.event = event;
		this.invariant = invariant;
	}
}
