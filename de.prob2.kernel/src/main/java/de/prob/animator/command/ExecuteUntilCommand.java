package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.animator.domainobjects.LTL;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.Transition;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

/**
 * Searches the StateSpace to find a trace. Operations are randomly executed
 * until a provided LTL formula becomes true.
 * 
 * @author joy
 * 
 */
public class ExecuteUntilCommand extends AbstractCommand implements
		IStateSpaceModifier, ITraceDescription {

	private static final String TRACE_VARIABLE = "Trace";
	private static final String RESULT_VARIABLE = "Result";
	private final List<Transition> resultTrace = new ArrayList<Transition>();
	private final State startstate;
	private final LTL condition;
	private final StateSpace statespace;
	private PrologTerm result;

	public ExecuteUntilCommand(final StateSpace statespace,
			final State startstate, final LTL condition) {
		this.statespace = statespace;
		this.startstate = startstate;
		this.condition = condition;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("generate_trace_until_condition_fulfilled");
		pout.printAtomOrNumber(this.startstate.getId());
		condition.printProlog(pout);
		pout.printVariable(TRACE_VARIABLE);
		pout.printVariable(RESULT_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm trace = BindingGenerator.getList(bindings
				.get(TRACE_VARIABLE));

		result = bindings.get(RESULT_VARIABLE);

		for (PrologTerm term : trace) {
			CompoundPrologTerm t = BindingGenerator.getCompoundTerm(term, 4);
			Transition operation = Transition.createOpInfoFromCompoundPrologTerm(
					statespace, t);
			resultTrace.add(operation);
		}
	}

	@Override
	public List<Transition> getNewTransitions() {
		return resultTrace;
	}

	public State getFinalState() {
		return resultTrace.get(resultTrace.size() - 1).getDestId();
	}

	@Override
	public Trace getTrace(final StateSpace s) throws RuntimeException {
		Trace t = s.getTrace(startstate.getId());
		return t.addTransitions(resultTrace);
	}

	/**
	 * @return if the command successfully found a trace to a state in which the
	 *         condition holds.
	 */
	public boolean isSuccess() {
		return result.getFunctor().equals("ltl_found");
	}

	/**
	 * @return if the maximum number of animation steps was reached before a
	 *         state was found in which the condition holds.
	 */
	public boolean conditionNotReached() {
		return result.getFunctor().equals("maximum_nr_of_steps_reached");
	}

	/**
	 * @return if the formula for the condition contains a type error
	 */
	public boolean hasTypeError() {
		return result.getFunctor().equals("typeerror");
	}

	/**
	 * @return if a deadlock was uncovered before a state was found in which the
	 *         condition holds.
	 */
	public boolean isDeadlocked() {
		return result.getFunctor().equals("deadlock");
	}
}
