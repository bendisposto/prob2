package de.prob.check;

public class ConstraintBasedCheckingResult {

	private Result r;

	public enum Result {
		no_deadlock_found, errors, interrupted, deadlock, no_invariant_violation_found, invariant_violation
	}
	
	public ConstraintBasedCheckingResult(Result r) {
		this.r = r;
	}
	
	public Result getResult() {
		return r;
	}
}
