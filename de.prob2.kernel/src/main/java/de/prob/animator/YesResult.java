package de.prob.animator;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.term.PrologTerm;

public class YesResult implements IPrologResult {

	private final ISimplifiedROMap<String, PrologTerm> bindings;

	public YesResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		this.bindings = bindings;
	}

	public ISimplifiedROMap<String, PrologTerm> getBindings() {
		return bindings;
	}

	@Override
	public String toString() {
		return "Yes: " + bindings.toString();
	}
}
