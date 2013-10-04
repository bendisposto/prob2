package de.prob.model.eventb.proof;

import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public abstract class ProofObligation extends AbstractElement {

	private final String name;
	private final boolean discharged;
	private final String description;
	private final String sourceName;

	public ProofObligation(final String sourceName, final String name,
			final boolean discharged, final String description) {
		this.sourceName = sourceName;
		this.name = name;
		this.discharged = discharged;
		this.description = description;
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
		printElements(pto);
		pto.closeList();
		pto.printAtom(String.valueOf(discharged));
		pto.closeTerm();
	}

	protected abstract void printElements(IPrologTermOutput pto);

}