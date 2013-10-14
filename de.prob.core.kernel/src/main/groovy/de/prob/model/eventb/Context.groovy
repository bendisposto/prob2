package de.prob.model.eventb;

import de.prob.model.eventb.proof.ProofObligation
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Axiom
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant

public class Context extends AbstractElement {

	private final String name;
	def List<ProofObligation> proofs
	def List<Context> Extends
	def List<BSet> sets
	def List<EventBAxiom> axioms
	def List<EventBConstant> constants
	private final String directoryPath;

	public Context(final String name, final String directoryPath) {
		this.directoryPath = directoryPath;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addExtends(final List<Context> contexts) {
		put(Context.class, contexts);
		Extends = contexts
	}

	public void addSets(final List<BSet> sets) {
		put(BSet.class, sets);
		this.sets = sets
	}

	public void addConstants(final List<EventBConstant> constants) {
		put(Constant.class, constants);
		this.constants = constants
	}

	public void addAxioms(final List<EventBAxiom> axioms, List<EventBAxiom> inherited) {
		inherited.addAll(axioms)
		put(Axiom.class, inherited);
		this.axioms = axioms
	}

	public void addProofs(final List<? extends ProofObligation> proofs) {
		put(ProofObligation.class, proofs);
		this.proofs = proofs
	}

	def List<ProofObligation> getProofs() {
		//TODO: Implement way to translate from UncalculatedPO to CalculatedPO
		return proofs
	}

	def List<ProofObligation> getRawProofs() {
		return proofs
	}

	@Override
	public String toString() {
		return name;
	}

	def EventBAxiom getAxiom(String name) {
		return axioms[name]
	}

	def EventBConstant getConstant(String name) {
		return constants[name]
	}

	def Context getExtendedContext(String name) {
		return Extends[name]
	}
}
