package de.prob.animator.domainobjects;

import java.util.Map;
import java.util.Set;

import de.hhu.stups.prob.translator.BValue;

import groovy.lang.MissingPropertyException;

public class TranslatedEvalResult extends AbstractEvalResult {
	private final BValue value;
	private final Map<String, BValue> solutions;

	public TranslatedEvalResult(final BValue value, final Map<String, BValue> solutions) {
		super();
		this.value = value;
		this.solutions = solutions;
	}

	public Map<String, BValue> getSolutions() {
		return solutions;
	}

	/**
	 * Tries to access a solution with the given name for the result.
	 *
	 * @param name of solution
	 * @return Object representation of solution, or {@code null} if the solution does not exist
	 */
	public BValue getSolution(final String name) {
		return solutions.get(name);
	}

	@Override
	public Object getProperty(final String property) {
		try {
			return super.getProperty(property);
		} catch (MissingPropertyException e) {
			if (this.getSolutions().containsKey(property)) {
				return this.getSolution(property);
			} else {
				throw e;
			}
		}
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public BValue getValue() {
		return value;
	}

	public Set<String> getKeys() {
		return solutions.keySet();
	}
}
