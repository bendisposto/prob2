/**
 * 
 */
package de.prob.animator.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.check.ConstraintBasedCheckingResult;
import de.prob.exception.ProBError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;

/**
 * This command makes ProB search for a deadlock with an optional predicate to
 * limit the search space.
 * 
 * @author plagge
 */
public class ConstraintBasedDeadlockCheckCommand extends AbstractCommand {

	Logger logger = LoggerFactory
			.getLogger(ConstraintBasedDeadlockCheckCommand.class);

	private static final String COMMAND_NAME = "deadlock_freedom_check";
	private static final String RESULT_VARIABLE = "R";

	private ConstraintBasedCheckingResult result;
	private String deadlockStateId;
	private OpInfo deadlockOperation;
	private final IEvalElement formula;

	/**
	 * @param formula
	 *            is a parsed predicate or <code>null</code>
	 * 
	 */
	public ConstraintBasedDeadlockCheckCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	public ConstraintBasedCheckingResult getResult() {
		return result;
	}

	public String getDeadlockStateId() {
		return deadlockStateId;
	}

	public OpInfo getDeadlockOperation() {
		return deadlockOperation;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		if (formula != null) {
			formula.printProlog(pto);
		}
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ConstraintBasedCheckingResult result;
		if (resultTerm.hasFunctor("no_deadlock_found", 0)) {
			result = new ConstraintBasedCheckingResult(
					ConstraintBasedCheckingResult.Result.no_deadlock_found);
		} else if (resultTerm.hasFunctor("errors", 1)) {
			result = new ConstraintBasedCheckingResult(
					ConstraintBasedCheckingResult.Result.errors);
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = new ConstraintBasedCheckingResult(
					ConstraintBasedCheckingResult.Result.interrupted);
		} else if (resultTerm.hasFunctor("deadlock", 2)) {

			CompoundPrologTerm deadlockTerm = BindingGenerator.getCompoundTerm(
					resultTerm, 2);
			result = new ConstraintBasedCheckingResult(
					ConstraintBasedCheckingResult.Result.deadlock);

			deadlockOperation = new OpInfo(BindingGenerator.getCompoundTerm(
					deadlockTerm.getArgument(1), 8));
			deadlockStateId = deadlockTerm.getArgument(2).toString();

		} else {
			String msg = "unexpected result from deadlock check: " + resultTerm;
			logger.error(msg);
			throw new ProBError(msg);
		}
		this.result = result;

	}
}
