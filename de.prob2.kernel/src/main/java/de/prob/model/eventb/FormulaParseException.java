package de.prob.model.eventb;

public class FormulaParseException extends ModelGenerationException {
	/**
	 *
	 */
	private static final long serialVersionUID = -5076084942068072351L;
	private String formula;

	public FormulaParseException(String formula) {
		this.formula = formula;
	}

	@Override
	public String getMessage() {
		return "Could not parse formula: " + formula;
	};

	public String getFormula() {
		return formula;
	}
}
