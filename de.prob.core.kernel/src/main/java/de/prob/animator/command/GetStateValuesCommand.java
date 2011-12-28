/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

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
	private List<Variable> result;

	public GetStateValuesCommand(final String stateID) {
		stateId = stateID;
	}

	public List<Variable> getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		final List<Variable> variables = new ArrayList<Variable>();

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
			variables.add(new Variable(compoundTerm));
		}
		result = variables;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getStateValues").printAtomOrNumber(stateId)
				.printVariable("Bindings").closeTerm();
	}

}
