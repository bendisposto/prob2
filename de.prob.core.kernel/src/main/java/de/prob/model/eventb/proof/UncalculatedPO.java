package de.prob.model.eventb.proof;

import java.util.List;

import org.parboiled.common.Tuple2;

import de.prob.prolog.output.IPrologTermOutput;

public class UncalculatedPO extends ProofObligation {
	private final List<Tuple2<String, String>> elements;

	public UncalculatedPO(final String source, final String name,
			final String desc, final List<Tuple2<String, String>> elements,
			final boolean discharged) {
		super(source, name, discharged, desc);
		this.elements = elements;
	}

	@Override
	protected void printElements(final IPrologTermOutput pto) {
		for (Tuple2<String, String> element : elements) {
			pto.openTerm(element.a);
			pto.printAtom(element.b);
			pto.closeTerm();
		}
	}

}
