package de.prob.animator.domainobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.prolog.term.CompoundPrologTerm;

public class Variable {
	Logger logger = LoggerFactory.getLogger(Variable.class);

	public final String name;
	public final String value;
	
	
	
	public Variable(final CompoundPrologTerm compoundTerm) {
		if (compoundTerm.getFunctor().equals("binding")) {
			name = ((CompoundPrologTerm) compoundTerm.getArgument(1))
					.getFunctor();
			value = ((CompoundPrologTerm) compoundTerm.getArgument(3))
					.getFunctor();
		} else {
			String msg = "Unexpected functor in Prolog answer. Expected 'binding' but was '"
					+ compoundTerm.getFunctor() + "'";
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public String toString() {
		return name + "->" + value;
	}

}
