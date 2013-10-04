package de.prob.model.eventb.proof;

import java.util.List;

import de.prob.prolog.output.IPrologTermOutput;

public class UncalculatedPO extends ProofObligation {
	private final List<Tuple> elements;

	public UncalculatedPO(final String source, final String name,
			final String desc, final List<Tuple> elements,
			final boolean discharged) {
		super(source, name, discharged, desc);
		this.elements = elements;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
		for (Tuple element : elements) {
			pto.openTerm(element.getType());
			pto.printAtom(element.getValue());
			pto.closeTerm();
		}
	}

}
