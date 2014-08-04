package de.prob.animator.domainobjects;

import com.google.common.base.Joiner

import de.prob.prolog.term.PrologTerm
import de.prob.unicode.UnicodeTranslator

public class EvalResult implements IEvalResult {

	private final String value;
	private final Map<String, String> solutions;
	private final Map<String, PrologTerm> solutionsSource;
	private final String code;

	public EvalResult(final String code, final String value,
	final Map<String, String> solutions,
	final Map<String, PrologTerm> solutionsSource) {
		this.code = code;
		this.value = value;
		this.solutions = solutions;
		this.solutionsSource = solutionsSource;
	}

	public String getValue() {
		return value;
	}

	public Map<String, String> getSolutions() {
		return solutions;
	}

	public Map<String, PrologTerm> getSolutionsSource() {
		return solutionsSource;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		if (solutions.isEmpty()) {
			return value;
		}
		def sols = solutions.collect { "${it.getKey()} = ${it.getValue()}" }

		return value + " (" + UnicodeTranslator.toUnicode(Joiner.on(" & ").join(sols)) + ")";
	}

	/**
	 * @param name
	 * @return This will search for a given solutionSource and translate it to a Groovy object
	 */
	def getProperty(String name) {
		if(solutionsSource.containsKey(name)) {
			return new ValueTranslator().toGroovy(solutionsSource[name])
		}
		return getMetaClass().getProperty(this, name)
	}
}
