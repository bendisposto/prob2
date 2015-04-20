package de.prob.check;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.LTL;

public class LTLNotYetFinished extends AbstractEvalResult implements
		IModelCheckingResult {

	private final LTL formula;

	public LTLNotYetFinished(final LTL formula) {
		super();
		this.formula = formula;
	}

	@Override
	public String getMessage() {
		return "LTL checking not complete.";
	}

	public String getCode() {
		return formula.getCode();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
