package de.prob.animator.domainobjects;

public class EvaluationResult {

	public final String value;
	public final String solution;

	public EvaluationResult(final String value, final String solution) {
		this.value = value;
		this.solution = solution;
	}

	@Override
	public String toString() {
		if (solution.equals(""))
			return value;
		else
			return value + " Solution: " + solution;
	}

}
