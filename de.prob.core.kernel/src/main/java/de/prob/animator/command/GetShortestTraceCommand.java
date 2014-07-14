package de.prob.animator.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateId;

public class GetShortestTraceCommand extends AbstractCommand {

	Logger logger = LoggerFactory.getLogger(GetShortestTraceCommand.class);

	private static final String TRACESTRINGS = "TraceStrings";
	private final StateId id;
	private final List<String> operationIds = new ArrayList<String>();

	public GetShortestTraceCommand(final StateId id) {
		this.id = id;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_trace_to_node");
		pto.printAtomOrNumber(id.getId());
		pto.printVariable(TRACESTRINGS);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm traceStrings = bindings.get(TRACESTRINGS);
		if (traceStrings instanceof ListPrologTerm) {
			for (PrologTerm term : (ListPrologTerm) traceStrings) {
				operationIds.add(term.getFunctor());
			}
		} else {
			String msg = "Trace was not found. Error was: "
					+ traceStrings.getFunctor();
			logger.error(msg);
			throw new RuntimeException(msg);
		}
	}

	public List<String> getOperationIds() {
		return operationIds;
	}
}
