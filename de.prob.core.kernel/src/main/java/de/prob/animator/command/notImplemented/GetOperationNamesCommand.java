/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command.notImplemented;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.model.representation.Operation;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetOperationNamesCommand implements ICommand {

	private static final String OPSPARAM = "OPS";
	Logger logger = LoggerFactory.getLogger(GetOperationNamesCommand.class);

	private final List<Operation> operations = new ArrayList<Operation>();

	public List<Operation> getOperations() {
		return operations;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("get_operation_infos").printVariable(OPSPARAM).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {

		try {
			ListPrologTerm ops = BindingGenerator.getList(bindings
					.get(OPSPARAM));
			for (PrologTerm prologTerm : ops) {
				CompoundPrologTerm op = BindingGenerator.getCompoundTerm(
						prologTerm, 2);
				String name = extractOperationName(op);
				ArrayList<String> list = extractOperationArguments(op);
				operations.add(new Operation(name, list, null));
			}
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}

	}

	private ArrayList<String> extractOperationArguments(
			final CompoundPrologTerm op) throws ResultParserException {
		ArrayList<String> list = new ArrayList<String>();
		ListPrologTerm arguments1 = BindingGenerator.getList(op.getArgument(2));
		for (PrologTerm term : arguments1) {
			String argname = BindingGenerator.getCompoundTerm(term, 0)
					.getFunctor();
			list.add(argname);
		}
		return list;
	}

	private String extractOperationName(final CompoundPrologTerm op)
			throws ResultParserException {
		String name = BindingGenerator.getCompoundTerm(op.getArgument(1), 0)
				.getFunctor();
		return name;
	}
}
