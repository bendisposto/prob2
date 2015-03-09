package de.prob.check;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLOk extends AbstractEvalResult implements IModelCheckingResult {

	private final LTL ltl;

	public LTLOk(final LTL ltl) {
		super();
		this.ltl = ltl;
	}

	@Override
	public String getMessage() {
		return "LTL status for " + ltl.getCode() + " : ok";
	}

	public String getCode() {
		return ltl.getCode();
	}

	@Override
	public String toString() {
		return getMessage();
	}

}
