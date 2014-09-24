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
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class ExecuteUntilCommand extends AbstractCommand implements
		IStateSpaceModifier, ITraceDescription {

	private static final String RESULT_VARIABLE = "Trace";
	private final List<OpInfo> resultTrace = new ArrayList<OpInfo>();
	private final StateId startstate;
	private final LTL condition;
	private final StateSpace statespace;

	public ExecuteUntilCommand(final StateSpace statespace,
			final StateId startstate, final LTL condition) {
		this.statespace = statespace;
		this.startstate = startstate;
		this.condition = condition;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pout) {
		pout.openTerm("find_trace");
		pout.printAtomOrNumber(this.startstate.getId());
		condition.printProlog(pout);
		pout.printVariable(RESULT_VARIABLE);
		pout.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm trace = BindingGenerator.getList(bindings
				.get(RESULT_VARIABLE));

		for (PrologTerm term : trace) {
			CompoundPrologTerm t = BindingGenerator.getCompoundTerm(term, 3);
			OpInfo operation = OpInfo.createOpInfoFromCompoundPrologTerm(
					statespace, t);
			resultTrace.add(operation);

		}
	}

	@Override
	public List<OpInfo> getNewTransitions() {
		return resultTrace;
	}

	public StateId getFinalState() {
		return resultTrace.get(resultTrace.size() - 1).getDestId();
	}

	@Override
	public Trace getTrace(final StateSpace s) throws RuntimeException {
		Trace t = s.getTrace(startstate);
		return t.addOps(resultTrace);
	}
}
