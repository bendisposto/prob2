/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.StateError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calculates the enabled operations, the state values, the initialization, the
 * invariant, the timeout, the operations with timeout, and the errors for a
 * given state.
 * 
 * @author joy
 * 
 */
public final class ExploreStateCommand extends AbstractCommand implements
		IStateSpaceModifier {

	Logger logger = LoggerFactory.getLogger(ExploreStateCommand.class);

	private final String stateId;
	private final GetEnabledOperationsCommand getOpsCmd;
	private final EvaluateRegisteredFormulasCommand evalFormulasCmd;
	private final CheckBooleanPropertyCommand checkInitialisedCmd;
	private final CheckInvariantStatusCommand checkInvCmd;
	private final CheckBooleanPropertyCommand checkMaxOpCmd;
	private final CheckBooleanPropertyCommand checkTimeoutCmd;
	private final GetStateBasedErrorsCommand getStateErrCmd;
	private final ComposedCommand allCommands;
	private final GetOperationsWithTimeout checkTimeoutOpsCmd;

	public ExploreStateCommand(final StateSpace s, final String stateID,
			final Collection<IEvalElement> formulas) {
		stateId = stateID;
		getOpsCmd = new GetEnabledOperationsCommand(s, stateId);
		evalFormulasCmd = new EvaluateRegisteredFormulasCommand(stateID,
				formulas);
		checkInitialisedCmd = new CheckInitialisationStatusCommand(stateId);
		checkInvCmd = new CheckInvariantStatusCommand(stateId);
		checkMaxOpCmd = new CheckMaxOperationReachedStatusCommand(stateId);
		checkTimeoutCmd = new CheckTimeoutStatusCommand(stateId);
		checkTimeoutOpsCmd = new GetOperationsWithTimeout(stateId);
		getStateErrCmd = new GetStateBasedErrorsCommand(stateId);
		allCommands = new ComposedCommand(getOpsCmd, evalFormulasCmd,
				checkInitialisedCmd, checkInvCmd, checkMaxOpCmd,
				checkTimeoutCmd, checkTimeoutOpsCmd, getStateErrCmd);

	}

	public String getStateID() {
		return stateId;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		allCommands.processResult(bindings);

		boolean initialised = checkInitialisedCmd.getResult();
		boolean timeoutOccured = checkTimeoutCmd.getResult();
		List<Transition> enabledOperations = getOpsCmd.getEnabledOperations();

		if (!initialised && enabledOperations.isEmpty() && !timeoutOccured) {
			logger.error("ProB could not find valid constants. This might be caused by the animation settings (e.g., Integer range or deferred set size) or by an inconsistency in the axioms");
			// TODO: Should an Error be thrown so that the StateSpace knows
			// about it?
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		allCommands.writeCommand(pto);
	}

	public boolean isInitialised() {
		return checkInitialisedCmd.getResult();
	}

	public boolean isInvariantOk() {
		return !checkInvCmd.isInvariantViolated();
	}

	public boolean isTimeoutOccured() {
		return checkTimeoutCmd.getResult();
	}

	public boolean isMaxOperationsReached() {
		return checkMaxOpCmd.getResult();
	}

	public Map<IEvalElement, AbstractEvalResult> getFormulaResults() {
		return evalFormulasCmd.getResults();
	}

	// TODO: The state errors calculated by ProB are never referenced by any
	// class
	public Collection<StateError> getStateErrors() {
		return getStateErrCmd.getResult();
	}

	public Set<String> getOperationsWithTimeout() {
		return new HashSet<>(checkTimeoutOpsCmd.getTimeouts());
	}

	@Override
	public List<AbstractCommand> getSubcommands() {
		List<AbstractCommand> subcommands = allCommands.getSubcommands();
		return subcommands;
	}

	@Override
	public List<Transition> getNewTransitions() {
		return getOpsCmd.getNewTransitions();
	}
}
