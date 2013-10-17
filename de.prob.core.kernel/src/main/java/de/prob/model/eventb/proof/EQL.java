package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventBVariable;
import de.prob.prolog.output.IPrologTermOutput;

public class EQL extends CalculatedPO {

	private final EventBVariable variable;
	private final Event event;

	public EQL(final String sourceName, final String proofName,
			final Event event, final EventBVariable variable,
			final EventB goal, final Set<EventB> hypotheses,
			final boolean discharged, final String description) {
		super(sourceName, proofName, discharged, description, goal, hypotheses);
		this.variable = variable;
		this.event = event;
	}

	public EventBVariable getVariable() {
		return variable;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
	}

}
