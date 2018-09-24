package de.prob.animator.domainobjects;

import java.util.Map;
import java.util.Set;

import de.prob.translator.types.BObject;

public class TranslatedEvalResult extends AbstractEvalResult {
	private final BObject value;
	private final Map<String, BObject> solutions;

	public TranslatedEvalResult(final BObject value, final Map<String, BObject> solutions) {
		super();
		this.value = value;
		this.solutions = solutions;
	}

	public Map<String, BObject> getSolutions() {
		return solutions;
	}

	/**
	 * Tries to access a solution with the given name for the result.
	 *
	 * @param name of solution
	 * @return Object representation of solution, or {@code null} if the solution does not exist
	 */
	public BObject getSolution(final String name) {
		return solutions.get(name);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public BObject getValue() {
		return value;
	}

	public Set<String> getKeys() {
		return solutions.keySet();
	}
}
