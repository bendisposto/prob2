package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.ITraceDescription;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;

public class FindTraceBetweenNodesCommand extends AbstractCommand implements
		ITraceDescription, IStateSpaceModifier {

	Logger logger = LoggerFactory.getLogger(FindTraceBetweenNodesCommand.class);

	private static final String TRACE = "Trace";

	List<OpInfo> newTransitions = new ArrayList<OpInfo>();
	private final StateSpace stateSpace;
	private final String sourceId;
	private final String destId;

	public FindTraceBetweenNodesCommand(final StateSpace stateSpace,
			final String sourceId, final String destId) {
		this.stateSpace = stateSpace;
		this.sourceId = sourceId;
		this.destId = destId;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_trace_from_node_to_node");
		pto.printAtomOrNumber(sourceId);
		pto.printAtomOrNumber(destId);
		pto.printVariable(TRACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm trace = bindings.get(TRACE);
		if (trace instanceof ListPrologTerm) {
			for (PrologTerm term : (ListPrologTerm) trace) {
				newTransitions.add(OpInfo.createOpInfoFromCompoundPrologTerm(
						stateSpace, (CompoundPrologTerm) term));
			}
		} else {
			String msg = "Trace was not found. Error was: "
					+ trace.getFunctor();
			logger.error(msg);
			throw new RuntimeException(msg);
		}
	}

	@Override
	public List<OpInfo> getNewTransitions() {
		return newTransitions;
	}

	@Override
	public Trace getTrace(final StateSpace s) throws RuntimeException {
		if (newTransitions.isEmpty()) {
			return new Trace(s.getState(sourceId));
		}
		return Trace.getTraceFromOpList(s, newTransitions);
	}

}
