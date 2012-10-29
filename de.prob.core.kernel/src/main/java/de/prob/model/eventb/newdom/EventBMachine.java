package de.prob.model.eventb.newdom;

import java.util.List;

import de.prob.model.representation.newdom.AbstractEvent;
import de.prob.model.representation.newdom.Invariant;
import de.prob.model.representation.newdom.Machine;
import de.prob.model.representation.newdom.Variable;

public class EventBMachine extends Machine {
	public EventBMachine(final String name) {
		super(name);
	}

	public void addRefines(final List<EventBMachine> machine) {
		put(Machine.class, machine);
	}

	public void addSees(final List<Context> contexts) {
		put(Context.class, contexts);
	}

	public void addVariables(final List<EventBVariable> variables) {
		put(Variable.class, variables);
	}

	public void addInvariants(final List<EventBInvariant> invariants) {
		put(Invariant.class, invariants);
	}

	public void addVariant(final List<Variant> variant) {
		put(Variant.class, variant);
	}

	public void addEvents(final List<Event> events) {
		put(AbstractEvent.class, events);
	}
}
