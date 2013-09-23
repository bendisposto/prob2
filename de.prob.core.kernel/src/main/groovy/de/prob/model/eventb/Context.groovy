package de.prob.model.eventb;

import de.prob.model.eventb.proof.ProofObligation
import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Axiom
import de.prob.model.representation.BSet
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList

public class Context extends AbstractElement {

	private final String name;
	def List<ProofObligation> proofs
	def List<Context> Extends
	def List<BSet> sets
	def List<EventBAxiom> axioms
	def List<EventBConstant> constants

	public Context(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addExtends(final List<Context> contexts) {
		put(Context.class, contexts);
		Extends = new ModelElementList<Context>(contexts)
	}

	public void addSets(final List<BSet> sets) {
		put(BSet.class, sets);
		this.sets = new ModelElementList<BSet>(sets)
	}

	public void addConstants(final List<EventBConstant> constants) {
		put(Constant.class, constants);
		this.constants = new ModelElementList<EventBConstant>(constants)
	}

	public void addAxioms(final List<EventBAxiom> axioms) {
		put(Axiom.class, axioms);
		this.axioms = new ModelElementList<EventBAxiom>(axioms)
	}

	public void addProofs(final List<ProofObligation> proofs) {
		put(ProofObligation.class, proofs);
		this.proofs = new ModelElementList<ProofObligation>(proofs);
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
