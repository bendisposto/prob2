package de.prob.model.eventb.generate;

import de.hhu.stups.sablecc.patch.SourcePosition;

public class FormulaParseError extends RuntimeException {
	private static final long serialVersionUID = 4590296122582846115L;
	private final SourcePosition startPosition;
	private final SourcePosition endPosition;
	private final String formula;

	public FormulaParseError(SourcePosition startPosition,
			SourcePosition endPosition, String formula) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.formula = formula;
	}

	public SourcePosition getStartPosition() {
		return startPosition;
	}

	public SourcePosition getEndPosition() {
		return endPosition;
	}

	public String getFormula() {
		return formula;
	}

	@Override
	public String getMessage() {
		return startPosition + " could not parse formula: " + formula;
	}
}
