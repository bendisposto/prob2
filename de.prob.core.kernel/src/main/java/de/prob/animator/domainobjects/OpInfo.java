package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ResultParserException;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Stores the information for a given Operation. This includes operation id
 * (id), operation name (name), the source state (src), and the destination
 * state (dest), as well as a list of parameters.
 * 
 * @author joy
 * 
 */
public class OpInfo {
	public final String id;
	public final String name;
	public final String src;
	public final String dest;
	public final List<String> params = new ArrayList<String>();

	Logger logger = LoggerFactory.getLogger(OpInfo.class);

	public OpInfo(final String id, final String name, final String src,
			final String dest, final List<String> params) {
		this.id = id;
		this.name = name;
		this.src = src;
		this.dest = dest;
		if (params != null) {
			for (String string : params) {
				this.params.add(string);
			}
		}
	}

	public OpInfo(final CompoundPrologTerm opTerm) throws ResultParserException {
		String id = null, src = null, dest = null;
		id = getIdFromPrologTerm(opTerm.getArgument(1));
		src = getIdFromPrologTerm(opTerm.getArgument(3));
		dest = getIdFromPrologTerm(opTerm.getArgument(4));
		ListPrologTerm lpt = BindingGenerator.getList(opTerm.getArgument(6));
		for (PrologTerm prologTerm : lpt) {
			params.add(prologTerm.getFunctor());
		}

		this.id = id;
		this.name = PrologTerm.atomicString(opTerm.getArgument(2));
		this.src = src;
		this.dest = dest;
	}

	public static String getIdFromPrologTerm(final PrologTerm destTerm)
			throws ResultParserException {
		if (destTerm instanceof IntegerPrologTerm) {
			return BindingGenerator.getInteger(destTerm).getValue().toString();
		}
		return destTerm.getFunctor();
	}
}
