/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetStateValuesCommand implements ICommand {

	private final Logger logger = LoggerFactory
			.getLogger(GetStateBasedErrorsCommand.class);

	private final String stateId;
	private HashMap<String, String> values = new HashMap<String, String>();

	public GetStateValuesCommand(final String stateID) {
		stateId = stateID;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		ListPrologTerm list;
		try {
			list = BindingGenerator.getList(bindings, "Bindings");
		} catch (ResultParserException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ProBException();
		}

		for (PrologTerm term : list) {
			CompoundPrologTerm compoundTerm;
			try {
				compoundTerm = BindingGenerator.getCompoundTerm(term,
						"binding", 3);
			} catch (ResultParserException e) {
				logger.error(e.getLocalizedMessage(), e);
				throw new ProBException();
			}
			addValue(compoundTerm);
		}
	}

	private void addValue(CompoundPrologTerm cpt) throws ProBException {
		if (cpt.getFunctor().equals("binding")) {
			try {
				String name = BindingGenerator.getCompoundTerm(
						cpt.getArgument(1), 0).getFunctor();
				String value = BindingGenerator.getCompoundTerm(
						cpt.getArgument(3), 0).getFunctor();
				values.put(name, value);
			} catch (ResultParserException e) {
				logger.error("Result from Prolog was not as expected.", e);
				throw new ProBException();
			}
		} else {
			String msg = "Unexpected functor in Prolog answer. Expected 'binding' but was '"
					+ cpt.getFunctor() + "'";
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getStateValues").printAtomOrNumber(stateId)
				.printVariable("Bindings").closeTerm();
	}
	
	public HashMap<String,String> getResult() {
		return values;
	}

}
