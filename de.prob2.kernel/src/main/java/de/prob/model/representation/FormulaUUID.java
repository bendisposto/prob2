package de.prob.model.representation;

import de.prob.prolog.output.IPrologTermOutput;

public class FormulaUUID implements IFormulaUUID {
	static int count = 0;
	private final String uuid;

	public FormulaUUID() {
		this.uuid = "formula_" + ++count;
	}

	@Override
	public void printUUID(final IPrologTermOutput pto) {
		pto.printAtom(uuid);
	}

	@Override
	public String getUUID() {
		return uuid;
	}
}
