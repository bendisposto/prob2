package de.prob.model.representation;

import de.prob.prolog.output.IPrologTermOutput;

public interface IFormulaUUID {
	public void printUUID(IPrologTermOutput pto);

	public String getUUID();
}
