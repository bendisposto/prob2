package de.prob.check;

import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLOk implements IEvalResult, IModelCheckingResult {

	private final LTL ltl;

	public LTLOk(final LTL ltl) {
		this.ltl = ltl;
	}

	@Override
	public String getMessage() {
		return "LTL status for " + ltl.getCode() + " : ok";
	}

	@Override
	public String getCode() {
		return ltl.getCode();
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
