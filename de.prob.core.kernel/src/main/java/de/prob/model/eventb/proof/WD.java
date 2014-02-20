package de.prob.model.eventb.proof;

import java.util.Set;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.eventb.EventBAction;
import de.prob.model.eventb.EventBAxiom;
import de.prob.model.eventb.EventBGuard;
import de.prob.model.eventb.EventBInvariant;
import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public class WD extends CalculatedPO {

	private final AbstractElement originalFormula;

	public WD(final String sourceName, final String proofName,
			final AbstractElement originalFormula, final EventB goal,
			final Set<EventB> hypotheses, final boolean discharged,
			final String description) {
		super(sourceName, proofName, discharged, description, goal, hypotheses);
		this.originalFormula = originalFormula;
	}

	public AbstractElement getOriginalFormula() {
		return originalFormula;
	}

	@Override
	public void printElements(final IPrologTermOutput pto) {
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
