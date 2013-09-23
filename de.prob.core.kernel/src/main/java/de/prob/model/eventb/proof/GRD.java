package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBGuard;
import de.prob.prolog.output.IPrologTermOutput;

public class GRD extends ProofObligation {

	private final EventBGuard guard;
	private final Event event;

	public GRD(final String proofName, final Event event,
			final EventBGuard guard, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(proofName, goal, hypotheses, discharged, description);
		this.guard = guard;
		this.event = event;
	}

	public EventBGuard getGuard() {
		return guard;
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
		pto.openTerm("guard");
		pto.printAtom(guard.getName());
		pto.closeTerm();
		pto.openTerm("event");
		pto.printAtom(guard.getParentEvent().getName());
		pto.closeTerm();

	}
}
