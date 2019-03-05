package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.check.CBCFeasibleSequence;
import de.prob.check.CBCInfeasiblePath;
import de.prob.check.CheckError;
import de.prob.check.IModelCheckingResult;
import de.prob.check.NotYetFinished;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

public class ConstraintBasedSequenceCheckCommand extends AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(ConstraintBasedSequenceCheckCommand.class);

	private static final String COMMAND_NAME = "prob2_find_test_path";
	
	private static final String RESULT_VARIABLE = "R";
	
	private IModelCheckingResult result;
	
	private final StateSpace s;
	
	private final List<String> events;
	
	private final IEvalElement predicate;
	
	private final int timeout;
	
	public ConstraintBasedSequenceCheckCommand(final StateSpace s, final List<String> events, final IEvalElement predicate) {
		this.s = s;
		this.events = events;
		this.predicate = predicate;
		this.timeout = 200;
	}
	
	public ConstraintBasedSequenceCheckCommand(final StateSpace s, final List<String> events, final IEvalElement predicate, final int timeout) {
		this.s = s;
		this.events = events;
		this.predicate = predicate;
		this.timeout = timeout;
	}
	
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.openList();
		for (final String event : events) {
			pto.printAtom(event);
		}
		pto.closeList();
		if (predicate != null) {
			predicate.printProlog(pto);
		}
		pto.printNumber(timeout);
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		// TODO Auto-generated method stub
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		if (resultTerm.hasFunctor("errors", 1)) {
			PrologTerm error = resultTerm.getArgument(1);
			logger.error("CBC Sequence Check produced errors: {}", error);
			this.result = new CheckError(
				"CBC Sequence Check produced errors. See Log for details.");
		} else if (resultTerm.hasFunctor("interrupt", 0)) {
			this.result = new NotYetFinished("CBC Sequence Check was interrupted", -1);
		} else if(resultTerm.hasFunctor("timeout", 0)) {
			this.result = new NotYetFinished("CBC Sequence Check occurs a timeout", -1);
		} else if(resultTerm.hasFunctor("infeasible_path", 0)) {
			this.result = new CBCInfeasiblePath();
		} else if(resultTerm instanceof ListPrologTerm) {
			ListPrologTerm list = (ListPrologTerm) resultTerm;
			List<Transition> ids = new ArrayList<>();
			for(PrologTerm prologTerm : list) {
				ids.add(Transition.createTransitionFromCompoundPrologTerm(s, (CompoundPrologTerm) prologTerm));
			}
			this.result = new CBCFeasibleSequence(ids);
		} else {
			String msg = "unexpected result from sequence check: " + resultTerm;
			logger.error(msg);
			throw new ProBError(msg);
		}
	}
	
	public IModelCheckingResult getResult() {
		return result;
	}
	
}
