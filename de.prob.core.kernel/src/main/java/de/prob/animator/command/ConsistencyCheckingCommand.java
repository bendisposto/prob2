/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ConsistencyCheckingCommand implements ICommand {
	private final int time;
	private final List<String> options;
	private ModelCheckingResult<Result> result;
	
	Logger logger = LoggerFactory.getLogger(ConsistencyCheckingCommand.class);

	public static enum Result {
		ok(true), ok_not_all_nodes_considered(true), deadlock(true), invariant_violation(
				true), assertion_violation(true), not_yet_finished(false), state_error(
				true), well_definedness_error(true), general_error(true);
		// I assume true means we can stop the model checking
		private final boolean abort;

		private Result(final boolean abort) {
			this.abort = abort;
		}

		public boolean isAbort() {
			return abort;
		}
	}

	ConsistencyCheckingCommand(final int time, final List<String> options) {
		this.time = time;
		this.options = options;
	}

	private ModelCheckingResult<Result> getResult() {
		return result;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException {
		//Should the arity of this be 0?
		try {
			CompoundPrologTerm term = BindingGenerator.getCompoundTerm(bindings.get("Result"),0);
			result = new ModelCheckingResult<Result>(Result.class, term);
		} catch (ResultParserException e) {
			logger.error("Result from Prolog was not as expected.", e);
			throw new ProBException();
		}
		
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_modelchecking").printNumber(time).openList();
		for (String o : options) {
			pto.printAtom(o);
		}
		pto.closeList().printVariable("Result").closeTerm();
	}
}
