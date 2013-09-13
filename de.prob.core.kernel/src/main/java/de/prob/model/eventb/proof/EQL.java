package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBVariable;
import de.prob.prolog.output.IPrologTermOutput;

public class EQL extends SimpleProofNode implements IProofObligation {

	private final String name;
	private final EventBVariable variable;
	private final Event event;

	public EQL(final String proofName, final Event event,
			final EventBVariable variable, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(goal, hypotheses, discharged, description);
		name = proofName;
		this.variable = variable;
		this.event = event;
	}

	public String getName() {
		return name;
	}

	public EventBVariable getVariable() {
		return variable;
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
		// TODO Auto-generated method stub

	}

}
