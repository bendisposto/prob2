package de.prob.animator.domainobjects;

public class EvaluationResult {

	public final String value;
	public final String solution;
	public final String errors;

	public EvaluationResult(final String value, final String solution,
			final String errors) {
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

}
