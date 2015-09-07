package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;

public class FormulaTypeException extends ModelGenerationException {

	/**
	 *
	 */
	private static final long serialVersionUID = 492703593594896699L;
	private EventB formula;
	private String expected;

	public FormulaTypeException(EventB formula, String expected) {
		this.formula = formula;
		this.expected = expected;
	}

	@Override
	public String getMessage() {
		return "Expected " + formula.toString() + " to be of type " + expected;
	}

	public EventB getFormula() {
		return formula;
	}

	public String getExpected() {
		return expected;
	}
}
