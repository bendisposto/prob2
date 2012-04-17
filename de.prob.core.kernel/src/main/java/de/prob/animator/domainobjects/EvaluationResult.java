package de.prob.animator.domainobjects;

public class EvaluationResult {

	public final String value;
	public final String solution;
	public final String errors;
	private final String code;

	public EvaluationResult(final String code, final String value,
			final String solution, final String errors) {
		this.code = code;
		this.value = value;
		this.solution = solution;
		this.errors = errors;
	}

	@Override
	public String toString() {
		if (!errors.isEmpty())
			return "'Errors: " + errors + "'";
		else {
			if (solution.equals(""))
				return value;
			else
				return value + " Solution: " + solution;
		}
	}

	public String getCode() {
		return code;
	}

}
