package de.prob.model.eventb.generate;

import de.hhu.stups.sablecc.patch.SourcePosition;
import de.prob.animator.domainobjects.EventB;

public class FormulaTypeError extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1240768003086819313L;
	private SourcePosition startPosition;
	private SourcePosition endPosition;
	private EventB formula;
	private String expected;

	public FormulaTypeError(SourcePosition startPosition,
			SourcePosition endPosition, EventB formula, String expected) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.formula = formula;
		this.expected = expected;
	}

	public SourcePosition getStartPosition() {
		return startPosition;
	}

	public SourcePosition getEndPosition() {
		return endPosition;
	}

	public EventB getFormula() {
		return formula;
	}

	public String getExpected() {
		return expected;
	}

	@Override
	public String getMessage() {
		return startPosition.toString() + " expected " + formula.toString()
				+ " to have type " + expected;
	}
}
