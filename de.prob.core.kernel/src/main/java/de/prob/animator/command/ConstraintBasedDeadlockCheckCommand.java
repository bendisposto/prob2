/**
 * 
 */
package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.check.CBCDeadlockFound;
import de.prob.check.CheckError;
import de.prob.check.IModelCheckingResult;
import de.prob.check.ModelCheckOk;
import de.prob.check.NotYetFinished;
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
public class ConstraintBasedDeadlockCheckCommand extends AbstractCommand
		implements IStateSpaceModifier {

	Logger logger = LoggerFactory
			.getLogger(ConstraintBasedDeadlockCheckCommand.class);

	private static final String COMMAND_NAME = "prob2_deadlock_freedom_check";
	private static final String RESULT_VARIABLE = "R";

	private IModelCheckingResult result;
	private String deadlockStateId;
	private OpInfo deadlockOperation;
	private final IEvalElement formula;
	private final List<OpInfo> newOps = new ArrayList<OpInfo>();

	/**
	 * @param formula
	 *            is a parsed predicate or <code>null</code>
	 * 
	 */
	public ConstraintBasedDeadlockCheckCommand(final IEvalElement formula) {
		this.formula = formula;
	}

	public IModelCheckingResult getResult() {
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
		final IModelCheckingResult result;
		if (resultTerm.hasFunctor("no_deadlock_found", 0)) {
			result = new ModelCheckOk("No deadlock was found");
		} else if (resultTerm.hasFunctor("errors", 1)) {
			PrologTerm error = resultTerm.getArgument(1);
			logger.error("CBC Deadlock Check produced errors: "
					+ error.toString());
			result = new CheckError(
					"CBC Deadlock check produced errors. This was likely during the typechecking of "
							+ "the given predicate. See Log for details.");
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = new NotYetFinished("CBC Deadlock check was interrupted",
					-1);
		} else if (resultTerm.hasFunctor("deadlock", 2)) {

			CompoundPrologTerm deadlockTerm = BindingGenerator.getCompoundTerm(
					resultTerm, 2);

			OpInfo deadlockOperation = OpInfo
					.createOpInfoFromCompoundPrologTerm(BindingGenerator
							.getCompoundTerm(deadlockTerm.getArgument(1), 3));
			newOps.add(deadlockOperation);
			String deadlockStateId = deadlockTerm.getArgument(2).toString();

			result = new CBCDeadlockFound(deadlockStateId, deadlockOperation);
		} else {
			String msg = "unexpected result from deadlock check: " + resultTerm;
			logger.error(msg);
			throw new ProBError(msg);
		}
		this.result = result;
	}

	@Override
	public List<OpInfo> getNewTransitions() {
		return newOps;
	}
}
