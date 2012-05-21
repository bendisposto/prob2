package de.prob.animator.domainobjects;

public class EvaluationResult {

	public final String value;
	public final String solution;
	public final String errors;
	public final String code;
	public final String explanation;

	public EvaluationResult(final String code, final String value,
			final String solution, final String errors, boolean solutionMode) {
		this.code = code;
		this.value = value;
		this.solution = solution;
		this.errors = errors;
		if (!solutionMode && "TRUE".equals(value))
			this.explanation = "Solution";
		else
			this.explanation = solutionMode ? " Solution: "
					: " Counterexample: ";
	}

	@Override
	public String toString() {
		if (!errors.isEmpty())
			return "'Errors: " + errors + "'";
		else {
			if (solution.equals(""))
				return value;
			else
				return value + explanation + solution;
		}
	}

}
