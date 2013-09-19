package de.prob.model.eventb;

import de.prob.model.eventb.proof.SimpleProofNode
import de.prob.model.representation.BEvent
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

public class EventBMachine extends Machine {

	def List<Context> sees
	def List<EventBVariable> variables
	def List<EventBMachine> refines
	def List<Event> events
	def List<EventBInvariant> invariants
	def List<SimpleProofNode> proofs
	def Variant variant

	public EventBMachine(final String name) {
		super(name);
	}

	public void addRefines(final List<EventBMachine> refines) {
		put(Machine.class, refines);
		this.refines = new ModelElementList<EventBMachine>(refines)
	}

	public void addSees(final List<Context> sees) {
		put(Context.class, sees);
		this.sees = new ModelElementList<Context>(sees)
	}

	public void addVariables(final List<EventBVariable> variables) {
		put(Variable.class, variables);
		this.variables = new ModelElementList<EventBVariable>(variables)
	}

	public void addInvariants(final List<EventBInvariant> invariants) {
		put(Invariant.class, invariants);
		this.invariants = new ModelElementList<EventBInvariant>(invariants)
	}

	public void addVariant(final List<Variant> variant) {
		put(Variant.class, variant);
		this.variant = variant
	}

	public void addEvents(final List<Event> events) {
		put(BEvent.class, events);
		this.events = new ModelElementList<Event>(events)
	}

	public void addProofs(final List<? extends SimpleProofNode> proofs) {
		put(SimpleProofNode.class, proofs);
		this.proofs = new ModelElementList<SimpleProofNode>(proofs)
	}

	public List<Event> getOperations() {
		return events
	}

	def Event getEvent(String name) {
		for (Event e : events) {
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
