package de.prob.check;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLError extends  AbstractEvalResult implements IModelCheckingResult {

	private final LTL formula;
	private final String reason;

	public LTLError(final LTL formula, final String reason) {
		super();
		this.formula = formula;
		this.reason = reason;
	}

	@Override
	public String getMessage() {
		return reason;
	}

	public String getCode() {
		return formula.getCode();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
