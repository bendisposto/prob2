package de.prob.animator.command;

import de.prob.prolog.term.CompoundPrologTerm;

public class Variable {

	private final String name;
	private final String value;

	public Variable(final CompoundPrologTerm compoundTerm) {
		assert compoundTerm.getFunctor().equals("binding");
		name = ((CompoundPrologTerm) compoundTerm.getArgument(1)).getFunctor();
		value = ((CompoundPrologTerm) compoundTerm.getArgument(3)).getFunctor();
	}

	@Override
	public String toString() {
		return name + "->" + value;
	}
}
