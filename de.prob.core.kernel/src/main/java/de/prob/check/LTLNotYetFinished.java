package de.prob.check;

import de.prob.animator.domainobjects.IEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLNotYetFinished implements IEvalResult, IModelCheckingResult {

	private final LTL formula;

	public LTLNotYetFinished(final LTL formula) {
		this.formula = formula;
	}

	@Override
	public String getMessage() {
		return "LTL checking not complete.";
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
