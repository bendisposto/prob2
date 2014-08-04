package de.prob.model.eventb;

import java.util.List;

import org.parboiled.common.Tuple2;

import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public class ProofObligation extends AbstractElement {

	private final String name;
	private final boolean discharged;
	private final String description;
	private final String sourceName;
	private final List<Tuple2<String, String>> elements;

	public ProofObligation(final String sourceName, final String name,
			final boolean discharged, final String description,
			final List<Tuple2<String, String>> elements) {
		this.sourceName = sourceName;
		this.name = name;
		this.discharged = discharged;
		this.description = description;
		this.elements = elements;
	}

	public String getName() {
		return name;
	}

	/**
	 * This method writes the source elements contained in a Proof Obligation in
	 * the given {@link IPrologTermOutput}. If certain elements are needed for a
	 * given proof obligation, then this proof obligation must override this
	 * method.
	 * 
	 * @param pto
	 */
	public void toProlog(final IPrologTermOutput pto) {
		pto.openTerm("po");
		pto.printAtom(sourceName);
		pto.printAtom(description);
		pto.openList();
		for (Tuple2<String, String> element : elements) {
			pto.openTerm(element.a);
			pto.printAtom(element.b);
			pto.closeTerm();
		}
		pto.closeList();
		pto.printAtom(String.valueOf(discharged));
		pto.closeTerm();
	}
}