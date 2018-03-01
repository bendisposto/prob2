package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.Transition;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class FindStateCommand extends AbstractCommand implements IStateSpaceModifier, ITraceDescription {
	public enum ResultType {
		STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR
	}

	private static final String PROLOG_COMMAND_NAME = "find_state_for_predicate";
	private static final String RESULT_VARIABLE = "R";

	private final IEvalElement predicate;

	private ResultType result;
	private String stateId;
	private Transition operation;
	private final StateSpace s;
	private final boolean onlyValidState;

	/**
	 * @param predicate is a parsed predicate or {@code null}
	 * @see ProBParserBase#parsePredicate(IPrologTermOutput, String, boolean)
	 */
	public FindStateCommand(final StateSpace s, final IEvalElement predicate, boolean onlyValidState) {
		this.s = s;
		this.predicate = predicate;
		this.onlyValidState = onlyValidState;
	}

	public ResultType getResult() {
		return result;
	}

	public String getStateId() {
		return stateId;
	}

	public Transition getOperation() {
		return operation;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		if (predicate != null) {
			predicate.printProlog(pto);
		}
		pto.printAtom(onlyValidState ? "true" : "false");
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);

		if (resultTerm.hasFunctor("no_valid_state_found", 0)) {
			this.result = ResultType.NO_STATE_FOUND;
		} else if (resultTerm.hasFunctor("errors", 1)) {
			this.result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			this.result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("state_found", 2)) {
			CompoundPrologTerm term = (CompoundPrologTerm) resultTerm;
			this.result = ResultType.STATE_FOUND;
			operation = Transition.createTransitionFromCompoundPrologTerm(s, (CompoundPrologTerm) term.getArgument(1));
			stateId = term.getArgument(2).toString();
		} else {
			throw new ProBError("unexpected result when trying to find a valid state: " + resultTerm);
		}
	}

	@Override
	public List<Transition> getNewTransitions() {
		List<Transition> ops = new ArrayList<>();
		if (operation != null) {
			ops.add(operation);
		}
		return ops;
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		if (stateId != null && result.equals(ResultType.STATE_FOUND)) {
			Trace t = s.getTrace(stateId);
			if (t != null) {
				return t;
			}
		}
		throw new NoStateFoundException("Was not able to produce a valid trace to the state specified by predicate: "
				+ predicate.getCode() + " Result type was: " + result);
	}
}
