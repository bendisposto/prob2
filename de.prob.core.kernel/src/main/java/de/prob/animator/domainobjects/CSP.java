package de.prob.animator.domainobjects;


import de.prob.model.representation.AbstractElement;
import de.prob.prolog.output.IPrologTermOutput;

public class CSP implements IEvalElement {

	private String code;

	public CSP(String formula) {
		this.code = formula;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void printProlog(IPrologTermOutput pout, AbstractElement m) {
	}

	@Override
	public String getKind() {
		return "csp";
	}


}
