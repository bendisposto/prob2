package de.prob.animator.command;

import java.util.List;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parserbase.ProBParserBase;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * @deprecated Use FindStateCommand instead
 *
 */
@Deprecated
public class FindValidStateCommand extends AbstractCommand implements IStateSpaceModifier, ITraceDescription {

	private FindStateCommand cmd;

	public static enum ResultType {
		STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR
	}

	/**
	 * @param predicate
	 *            is a parsed predicate or <code>null</code>
	 * @see ProBParserBase#parsePredicate(IPrologTermOutput, String, boolean)
	 */
	public FindValidStateCommand(final StateSpace s, final IEvalElement predicate) {
		this.cmd = new FindStateCommand(s, predicate, true);
	}

	public ResultType getResult() {
		return ResultType.valueOf(cmd.getResult().toString());
	}

	public String getStateId() {
		return cmd.getStateId();
	}

	public Transition getOperation() {
		return cmd.getOperation();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		cmd.writeCommand(pto);
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		cmd.processResult(bindings);
	}

	@Override
	public List<Transition> getNewTransitions() {
		return cmd.getNewTransitions();
	}

	@Override
	public Trace getTrace(final StateSpace s) {
		return cmd.getTrace(s);
	}
}
