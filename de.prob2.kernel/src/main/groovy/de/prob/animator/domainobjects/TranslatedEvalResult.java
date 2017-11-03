package de.prob.animator.domainobjects;

import de.prob.translator.types.BObject;

import java.util.Map;
import java.util.Set;

public class TranslatedEvalResult extends AbstractEvalResult {
	public TranslatedEvalResult(Object value, Map<String, BObject> solutions) {
		super();
		this.value = ((BObject) (value));
		this.solutions = solutions;
	}

	/**
	 * Tries to access a solution with the given name for the result.
	 *
	 * @param name of solution
	 * @return Object representation of solution, or <code>null</code> if the solution does not exist
	 */
	public BObject getSolution(String name) {
		return (solutions.get(name));
	}

	public String toString() {
		return value.toString();
	}

	public BObject getValue() {
		return value;
	}

	public Set<String> getKeys() {
		return solutions.keySet();
	}

	public void setValue(BObject value) {
		this.value = value;
	}

	public Map<String, BObject> getSolutions() {
		return solutions;
	}

	public void setSolutions(Map<String, BObject> solutions) {
		this.solutions = solutions;
	}

	private BObject value;
	private Map<String, BObject> solutions;
}
