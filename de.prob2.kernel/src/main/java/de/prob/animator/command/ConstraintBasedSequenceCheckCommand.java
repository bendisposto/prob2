package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class ConstraintBasedSequenceCheckCommand extends AbstractCommand implements IStateSpaceModifier {

	
	public enum ResultType {
		PATH_FOUND, NO_PATH_FOUND, TIMEOUT, INTERRUPTED, ERROR
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ConstraintBasedSequenceCheckCommand.class);

	private static final String COMMAND_NAME = "prob2_find_test_path";
	
	private static final String RESULT_VARIABLE = "R";
	
	private ResultType result;
	
	private final StateSpace s;
	
	private final List<String> events;
	
	private final IEvalElement predicate;
	
	private final int timeout;
	
	private List<Transition> transitions;
	
	public ConstraintBasedSequenceCheckCommand(final StateSpace s, final List<String> events, final IEvalElement predicate) {
		this.s = s;
		this.events = events;
		this.predicate = predicate;
		this.timeout = 200;
		this.transitions = new ArrayList<>();
	}
	
	public ConstraintBasedSequenceCheckCommand(final StateSpace s, final List<String> events, final IEvalElement predicate, final int timeout) {
		this.s = s;
		this.events = events;
		this.predicate = predicate;
		this.timeout = timeout;
		this.transitions = new ArrayList<>();
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
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		if (resultTerm.hasFunctor("errors", 1)) {
			PrologTerm error = resultTerm.getArgument(1);
			logger.error("CBC Sequence Check produced errors: {}", error);
			this.result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupt", 0)) {
			this.result = ResultType.INTERRUPTED;
		} else if(resultTerm.hasFunctor("timeout", 0)) {
			this.result = ResultType.TIMEOUT;
		} else if(resultTerm.hasFunctor("infeasible_path", 0)) {
			this.result = ResultType.NO_PATH_FOUND;
		} else if(resultTerm instanceof ListPrologTerm) {
			ListPrologTerm list = (ListPrologTerm) resultTerm;
			this.result = ResultType.PATH_FOUND;
			List<Transition> transitions = new ArrayList<>();
			for(PrologTerm prologTerm : list) {
				transitions.add(Transition.createTransitionFromCompoundPrologTerm(s, (CompoundPrologTerm) prologTerm));
			}
			this.transitions = transitions;
		} else {
			String msg = "unexpected result from sequence check: " + resultTerm;
			logger.error(msg);
			throw new ProBError(msg);
		}
	}
	
	public ResultType getResult() {
		return result;
	}

	public Trace getTrace() {
		if(transitions.isEmpty()) {
			return null;
		}
		return s.getTrace(transitions.stream()
				.map(Transition::getId)
				.collect(Collectors.toList()));
	}

	@Override
	public List<Transition> getNewTransitions() {
		return transitions;
	}
	
}
