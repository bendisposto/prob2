package de.prob.model.eventb.proof;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.eventb.translate.ProofTreeCreator;
import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public class WD extends ProofObligation {

	private final AbstractElement originalFormula;

	public WD(final String proofName, final AbstractElement originalFormula,
			final EventB goal, final boolean discharged,
			final String description, final ProofTreeCreator creator) {
		super(proofName, goal, discharged, description, creator);
		this.originalFormula = originalFormula;
	}

	public AbstractElement getOriginalFormula() {
		return originalFormula;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void toProlog(final IPrologTermOutput pto) {
		if (originalFormula instanceof EventBInvariant) {
			pto.openTerm("invariant");
			pto.printAtom(((EventBInvariant) originalFormula).getName());
			pto.closeTerm();
		}
		if (originalFormula instanceof EventBAxiom) {
			pto.openTerm("axiom");
			pto.printAtom(((EventBAxiom) originalFormula).getName());
			pto.closeTerm();
		}
		if (originalFormula instanceof EventBAction) {
			pto.openTerm("action");
			pto.printAtom(((EventBAction) originalFormula).getName());
			pto.closeTerm();
		}
		if (originalFormula instanceof EventBGuard) {
			pto.openTerm("guard");
			pto.printAtom(((EventBGuard) originalFormula).getName());
			pto.closeTerm();
		}
	}
}
