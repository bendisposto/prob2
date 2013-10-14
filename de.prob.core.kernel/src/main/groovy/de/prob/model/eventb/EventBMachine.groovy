package de.prob.model.eventb;

import de.prob.model.eventb.proof.ProofObligation
import de.prob.model.representation.BEvent
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.Variable

public class EventBMachine extends Machine {

	def List<Context> sees
	def List<EventBVariable> variables
	def List<EventBMachine> refines
	def List<Event> events
	def List<EventBInvariant> invariants
	def List<ProofObligation> proofs
	def Variant variant
	private final String directoryPath

	public EventBMachine(final String name, final String directoryPath) {
		super(name)
		this.directoryPath = directoryPath;
	}

	public void addRefines(final List<EventBMachine> refines) {
		put(Machine.class, refines);
		this.refines = refines
	}

	public void addSees(final List<Context> sees) {
		put(Context.class, sees);
		this.sees = sees
	}

	public void addVariables(final List<EventBVariable> variables) {
		put(Variable.class, variables);
		this.variables = variables
	}

	public void addInvariants(final List<EventBInvariant> invariants, final List<EventBInvariant> inherited) {
		inherited.addAll(invariants)
		put(Invariant.class, inherited);
		this.invariants = invariants
	}

	public void addVariant(final List<Variant> variant) {
		put(Variant.class, variant);
		this.variant = variant.isEmpty() ? null : variant[0]
	}

	public void addEvents(final List<Event> events) {
		put(BEvent.class, events);
		this.events = events
	}

	public void addProofs(final List<? extends ProofObligation> proofs) {
		put(ProofObligation.class, proofs);
		this.proofs = proofs
	}

	public List<Event> getOperations() {
		return events
	}

	def List<ProofObligation> getProofs() {
		//TODO: Implement way to translate from UncalculatedPO to CalculatedPO
		return proofs
	}

	def List<ProofObligation> getRawProofs() {
		return proofs
	}

	def Event getEvent(String name) {
		return events[name]
	}

	def EventBInvariant getInvariant(String name) {
		return invariants[name]
	}

	def EventBVariable getVariable(String name) {
		return invariants[name]
	}

	def EventBMachine getRefinedMachine(String name) {
		return refines[name]
	}

	def Context getSeenContext(String name) {
		return sees[name]
	}
}
