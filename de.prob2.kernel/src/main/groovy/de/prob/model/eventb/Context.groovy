package de.prob.model.eventb;

import de.prob.model.representation.AbstractElement
import de.prob.model.representation.Axiom
import de.prob.model.representation.Set
import de.prob.model.representation.Constant
import de.prob.model.representation.ModelElementList

public class Context extends AbstractElement {

	private final String name;
	def ModelElementList<ProofObligation> proofs
	def ModelElementList<Context> Extends
	def ModelElementList<Set> sets
	def ModelElementList<EventBAxiom> axioms
	def ModelElementList<EventBAxiom> allAxioms // includes inherited
	def ModelElementList<EventBConstant> constants
	private final String directoryPath;

	public Context(final String name, final String directoryPath) {
		this.directoryPath = directoryPath;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addExtends(final ModelElementList<Context> contexts) {
		put(Context.class, contexts);
		Extends = contexts
	}

	public void addSets(final ModelElementList<Set> sets) {
		put(Set.class, sets);
		this.sets = sets
	}

	public void addConstants(final ModelElementList<EventBConstant> constants) {
		put(Constant.class, constants);
		this.constants = constants
	}

	public void addAxioms(final ModelElementList<EventBAxiom> axioms, ModelElementList<EventBAxiom> inherited) {
		put(Axiom.class, axioms);
		inherited.addAll(axioms)
		this.axioms = axioms
		this.allAxioms = inherited
	}

	public void addProofs(final ModelElementList<ProofObligation> proofs) {
		put(ProofObligation.class, proofs);
		this.proofs = proofs
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
