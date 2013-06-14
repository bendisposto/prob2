package de.prob.model.eventb;

import de.prob.model.representation.BEvent
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

public class EventBMachine extends Machine {

	public EventBMachine(final String name) {
		super(name);
	}

	public void addRefines(final List<EventBMachine> refines) {
		put(Machine.class, refines);
	}

	public void addSees(final List<Context> sees) {
		put(Context.class, sees);
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
		put(BEvent.class, events);
	}

	public List<EventBVariable> getVariables() {
		List<EventBVariable> vars = new ModelElementList<EventBVariable>();
		Set<Variable> c = getChildrenOfType(Variable.class);
		for (Variable variable : c) {
			vars.add((EventBVariable) variable);
		}
		return vars;
	}

	public List<EventBInvariant> getInvariants() {
		List<EventBInvariant> invs = new ModelElementList<EventBInvariant>();
		Collection<Invariant> kids = getChildrenOfType(Invariant.class);
		for (Invariant invariant : kids) {
			if (invariant instanceof EventBInvariant) {
				invs.add((EventBInvariant) invariant);
			}
		}
		return invs;
	}

	public Variant getVariant() {
		Set<Variant> kids = getChildrenOfType(Variant.class);
		if (!kids.isEmpty()) {
			return kids.iterator().next();
		}
		return null;
	}

	public List<Event> getEvents() {
		List<Event> events = new ModelElementList<Event>();
		Set<BEvent> kids = getChildrenOfType(BEvent.class);
		for (BEvent bEvent : kids) {
			if (bEvent instanceof Event) {
				events.add((Event) bEvent);
			}
		}
		return events;
	}

	def Event getEvent(String name) {
		for (Event e : getEvents()) {
			if (e.getName().equals(name)) return e;
		}
		return null;
	}

	def getProperty(String prop) {
		if(prop == "variables") {
			return getVariables()
		} else if(prop == "invariants") {
			return getInvariants()
		} else if(prop == "variant") {
			return getVariant()
		} else if(prop == "events") {
			return getEvents()
		}
		Machine.getMetaClass().getProperty(this, prop)
	}
}
