package de.prob.check;

import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLError implements IEvalResult, IModelCheckingResult {

	private final LTL formula;
	private final String reason;

	public LTLError(final LTL formula, final String reason) {
		this.formula = formula;
		this.reason = reason;
	}

	@Override
	public String getMessage() {
		return reason;
	}

	@Override
	public String getCode() {
		return formula.getCode();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
