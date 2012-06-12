package de.prob.animator.domainobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
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
	public final String state;
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

		this.state = getString();
	}

	private String getString() {
		byte[] arr = new byte[20];
		Random r = new Random();
		r.nextBytes(arr);
		return new String(arr);
	}

	public OpInfo(final CompoundPrologTerm opTerm) throws ProBException {
		String id = null, src = null, dest = null;
		try {
			id = getIdFromPrologTerm(opTerm.getArgument(1));
			src = getIdFromPrologTerm(opTerm.getArgument(3));
			dest = getIdFromPrologTerm(opTerm.getArgument(4));
			ListPrologTerm lpt = BindingGenerator
					.getList(opTerm.getArgument(6));
			for (PrologTerm prologTerm : lpt) {
				params.add(prologTerm.getFunctor());
			}
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}

		this.id = id;
		this.name = PrologTerm.atomicString(opTerm.getArgument(2));
		this.src = src;
		this.dest = dest;
		this.state = getString();
	}

	public static String getIdFromPrologTerm(final PrologTerm destTerm)
			throws ResultParserException {
		if (destTerm instanceof IntegerPrologTerm) {
			return BindingGenerator.getInteger(destTerm).getValue().toString();
		}
		return destTerm.getFunctor();
	}
}
