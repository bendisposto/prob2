package de.prob.animator.command;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class ExecuteModelCommand extends AbstractCommand implements IStateSpaceModifier, ITraceDescription {

	private static final String PROLOG_COMMAND_NAME = "execute_model";
	private static final String TRANSITION_VARIABLE = "Transition";
	private static final String RESULT_VARIABLE = "Result";
	private static final String EXECUTED_STEPS_VARIABLE = "Steps";
	private final List<Transition> resultTrace = new ArrayList<>();
	private final State startstate;
	private final StateSpace statespace;
	private int stepsExecuted;
	private final int maxNrSteps;
	private ExecuteModelResult result;

	public enum ExecuteModelResult {
		MAXIMUM_NR_OF_STEPS_REACHED, DEADLOCK
	}

	public ExecuteModelCommand(final StateSpace statespace, final State startstate, final int maxNrSteps) {
		this.statespace = statespace;
		this.startstate = startstate;
		this.maxNrSteps = maxNrSteps;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm(PROLOG_COMMAND_NAME);
		pout.printAtomOrNumber(this.startstate.getId());
		pout.printAtomOrNumber(String.valueOf(maxNrSteps));
		pout.printVariable(TRANSITION_VARIABLE);
		pout.printVariable(EXECUTED_STEPS_VARIABLE);
		pout.printVariable(RESULT_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TRANSITION_VARIABLE);
		CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(prologTerm, 4);
		Transition operation = Transition.createTransitionFromCompoundPrologTerm(statespace, cpt);
		resultTrace.add(operation);

		IntegerPrologTerm intPrologTerm = BindingGenerator.getInteger(bindings.get(EXECUTED_STEPS_VARIABLE));
		BigInteger bigInt = intPrologTerm.getValue();
		stepsExecuted = bigInt.intValue();

		final String result = bindings.get(RESULT_VARIABLE).getFunctor();
		switch (result) {
		case "maximum_nr_of_steps_reached":
			this.result = ExecuteModelResult.MAXIMUM_NR_OF_STEPS_REACHED;
			break;
		case "deadlock":
			this.result = ExecuteModelResult.DEADLOCK;
			break;
		default:
			throw new AssertionError("Unexpected result of execute command.");
		}

	}

	@Override
	public List<Transition> getNewTransitions() {
		return resultTrace;
	}

	public State getFinalState() {
		return resultTrace.get(resultTrace.size() - 1).getDestination();
	}

	@Override
	public Trace getTrace(final StateSpace s) throws RuntimeException {
		Trace t = s.getTrace(startstate.getId());
		return t.addTransitions(resultTrace);
	}

	public int getNumberofStatesExecuted() {
		return this.stepsExecuted;
	}

	/**
	 * @return if a deadlock was uncovered before the maximum number of steps
	 *         was reached.
	 */
	public boolean isDeadlocked() {
		return result.equals(ExecuteModelResult.DEADLOCK);
	}

	/**
	 * @return the result of of the executeModelCommand. The result is Result is
	 *         either {@link ExecuteModelResult#MAXIMUM_NR_OF_STEPS_REACHED} or
	 *         {@link ExecuteModelResult#DEADLOCK}.
	 */

	public ExecuteModelResult getResult() {
		return this.result;
	}
}
