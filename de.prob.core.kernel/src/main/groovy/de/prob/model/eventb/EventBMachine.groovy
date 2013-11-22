package de.prob.model.eventb;

import de.prob.model.eventb.proof.ProofObligation
import de.prob.model.representation.BEvent
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.ModelElementList
import de.prob.model.representation.Variable

public class EventBMachine extends Machine {

	def ModelElementList<Context> sees = new ModelElementList<Context>()
	def ModelElementList<EventBVariable> variables = new ModelElementList<EventBVariable>()
	def ModelElementList<EventBMachine> refines = new ModelElementList<EventBMachine>()
	def ModelElementList<Event> events = new ModelElementList<Event>()
	def ModelElementList<EventBInvariant> invariants = new ModelElementList<EventBInvariant>()
	def ModelElementList<ProofObligation> proofs = new ModelElementList<ProofObligation>()
	def Variant variant
	private final String directoryPath

	public EventBMachine(final String name, final String directoryPath) {
		super(name)
		this.directoryPath = directoryPath;
	}

	public void addRefines(final ModelElementList<EventBMachine> refines) {
		put(Machine.class, refines);
		this.refines = refines
	}

	public void addSees(final ModelElementList<Context> sees) {
		put(Context.class, sees);
		this.sees = sees
	}

	public void addVariables(final ModelElementList<EventBVariable> variables) {
		put(Variable.class, variables);
		this.variables = variables
	}

	public void addInvariants(final ModelElementList<EventBInvariant> invariants, ModelElementList<EventBInvariant> inherited) {
		inherited.addAll(invariants)
		put(Invariant.class, inherited);
		this.invariants = invariants
	}

	public void addVariant(final ModelElementList<Variant> variant) {
		put(Variant.class, variant);
		this.variant = variant.isEmpty() ? null : variant[0]
	}

	public void addEvents(final ModelElementList<Event> events) {
		put(BEvent.class, events);
		this.events = events
	}

	public void addProofs(final ModelElementList<? extends ProofObligation> proofs) {
		put(ProofObligation.class, proofs);
		this.proofs = proofs
	}

	public ModelElementList<Event> getOperations() {
		return events
	}

	def ModelElementList<ProofObligation> getProofs() {
		//TODO: Implement way to translate from UncalculatedPO to CalculatedPO
		return proofs
	}

	def ModelElementList<ProofObligation> getRawProofs() {
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
