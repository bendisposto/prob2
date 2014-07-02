package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

public class FindValidStateCommand extends AbstractCommand {

	public static enum ResultType {
		STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR
	};

	private static final String COMMAND_NAME = "find_state_satisfying_predicate";
	private static final String RESULT_VARIABLE = "R";

	private final IEvalElement predicate;

	private ResultType result;
	private String stateId;
	private OpInfo operation;

	/**
	 * @param predicate
	 *            is a parsed predicate or <code>null</code>
	 * @see LanguageDependendAnimationPart#parsePredicate(IPrologTermOutput,
	 *      String, boolean)
	 */
	public FindValidStateCommand(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public ResultType getResult() {
		return result;
	}

	public String getStateId() {
		return stateId;
	}

	public OpInfo getOperation() {
		return operation;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		if (predicate != null) {
			predicate.printProlog(pto);
		}
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;

		if (resultTerm.hasFunctor("no_valid_state_found", 0)) {
			result = ResultType.NO_STATE_FOUND;
		} else if (resultTerm.hasFunctor("errors", 1)) {
			result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("state_found", 2)) {
			CompoundPrologTerm term = (CompoundPrologTerm) resultTerm;
			result = ResultType.STATE_FOUND;
			operation = OpInfo
					.createOpInfoFromCompoundPrologTerm((CompoundPrologTerm) term
							.getArgument(1));
			stateId = term.getArgument(2).toString();
		} else
			throw new ProBError(
					"unexpected result when trying to find a valid state: "
							+ resultTerm);

		this.result = result;
	}
}
