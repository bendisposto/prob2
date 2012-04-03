/**
 * 
 */
package de.prob.animator.command.notImplemented;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.animator.command.OpInfo;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command makes ProB search for a deadlock with an optional predicate to
 * limit the search space.
 * 
 * @author plagge
 */
public class ConstraintBasedDeadlockCheckCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(ConstraintBasedDeadlockCheckCommand.class);

	public static enum ResultType {
		DEADLOCK_FOUND, NO_DEADLOCK, ERROR, INTERRUPTED
	};

	private static final String COMMAND_NAME = "deadlock_freedom_check";
	private static final String RESULT_VARIABLE = "R";

	private final PrologTerm predicate;

	private ResultType result;
	private String deadlockStateId;
	private OpInfo deadlockOperation;

	/**
	 * @param predicate
	 *            is a parsed predicate or <code>null</code>
	 * @see LanguageDependendAnimationPart#parsePredicate(IPrologTermOutput,
	 *      String, boolean)
	 */
	public ConstraintBasedDeadlockCheckCommand(final PrologTerm predicate) {
		this.predicate = predicate;
	}

	public PrologTerm getPredicate() {
		return predicate;
	}

	public ResultType getResult() {
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
		if (predicate != null) {
			predicate.toTermOutput(pto);
		}
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;
		if (resultTerm.hasFunctor("no_deadlock_found", 0)) {
			result = ResultType.NO_DEADLOCK;
		} else if (resultTerm.hasFunctor("errors", 1)) {
			result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("deadlock", 2)) {

			try {
				CompoundPrologTerm deadlockTerm = BindingGenerator
						.getCompoundTerm(resultTerm, 2);
				result = ResultType.DEADLOCK_FOUND;

				deadlockOperation = new OpInfo(
						BindingGenerator.getCompoundTerm(
								deadlockTerm.getArgument(1), 7));
				deadlockStateId = deadlockTerm.getArgument(2).toString();

			} catch (ResultParserException e) {
				logger.error("Result from Prolog was not as expected.", e);
				throw new ProBException();
			}
			
		} else {
			logger.error("unexpected result from deadlock check: " + resultTerm);
			throw new ProBException();
		}
		this.result = result;

	}
}
