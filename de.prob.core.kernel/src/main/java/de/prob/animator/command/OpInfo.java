package de.prob.animator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class OpInfo {
	public final String id;
	public final String name;
	public final String src;
	public final String dest;
	public final String params;

	Logger logger = LoggerFactory.getLogger(OpInfo.class);
	
	public OpInfo(final String id, final String name, final String src,
			final String dest, final String params) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		this.params = params;
	}

	// FIXME: Implement this for OpInfo. Should it be a static method?
	public OpInfo(final CompoundPrologTerm opTerm) throws ProBException {
		// final CompoundPrologTerm opTerm = (CompoundPrologTerm) rawOpTerm;

		IntegerPrologTerm intTerm;

		String id;
		try {
			intTerm = BindingGenerator.getInteger(opTerm.getArgument(1));
			id = BindingGenerator.getCompoundTerm(intTerm, 0).getFunctor();
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
		
		
		this.id = id;
		this.name = PrologTerm.atomicString(opTerm.getArgument(2));
		this.src = getIdFromPrologTerm(opTerm.getArgument(3));
		this.dest = getIdFromPrologTerm(opTerm.getArgument(4));
		this.params = null;
		// final List<PrologTerm> args = (ListPrologTerm)
		// opTerm.getArgument(5);
		// FIXME: so what is params?
	}

	private static String getIdFromPrologTerm(final PrologTerm destTerm) {
		if (destTerm instanceof IntegerPrologTerm) {
			return ((IntegerPrologTerm) destTerm).getValue().toString();
		}
		return ((CompoundPrologTerm) destTerm).getFunctor();
	}
}
