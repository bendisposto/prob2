package de.prob.animator.domainobjects;

import de.prob.prolog.output.IPrologTermOutput;

public interface IEvalElement {
	public String getCode();

	public void printProlog(IPrologTermOutput pout);

	public String getType();
}
