package de.prob.model.representation;

import de.prob.prolog.output.IPrologTermOutput;

public class TranslateUUID implements IFormulaUUID {
	private final String uuid;

	public TranslateUUID(final IFormulaUUID uuid) {
		this.uuid = "t_" + uuid.getUUID();
	}

	@Override
	public void printUUID(final IPrologTermOutput pto) {
		pto.openTerm("translate");
		pto.printAtom(this.uuid);
		pto.closeTerm();
	}

	@Override
	public String getUUID() {
		return uuid;
	}
}
