package de.prob.model.eventb.generate;

import de.hhu.stups.sablecc.patch.SourcePosition;
import de.prob.animator.domainobjects.EventB;

public class FormulaTypeError extends RuntimeException {
	private static final long serialVersionUID = 1240768003086819313L;
	private final SourcePosition startPosition;
	private final SourcePosition endPosition;
	private final EventB formula;
	private final String expected;

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
		return startPosition + " expected " + formula + " to have type " + expected;
	}
}
