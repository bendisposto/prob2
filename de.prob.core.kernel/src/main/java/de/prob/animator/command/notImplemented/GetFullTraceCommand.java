/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command.notImplemented;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.command.ICommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetFullTraceCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetFullTraceCommand.class);

	private static final String IDS_VARIABLE = "IDs";
	private static final String ACTIONS_VARIABLE = "Actions";

	public class TraceResult {
		private final List<String> operations;
		private final List<String> states;

		public TraceResult(final List<String> operations,
				final List<String> states) {
			if (!(operations.size() == states.size())) {
				String msg = "Operations and states must be the same size";
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
			this.operations = Collections.unmodifiableList(operations);
			this.states = Collections.unmodifiableList(states);
		}

		public List<String> getOperations() {
			return operations;
		}

		public List<String> getStates() {
			return states;
		}
	}

	private TraceResult trace;

	private GetFullTraceCommand() {
	}

	public TraceResult getTrace() {
		return trace;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		List<String> operations = PrologTerm
				.atomicStrings((ListPrologTerm) bindings.get(ACTIONS_VARIABLE));
		List<String> states;
		states = getStateIDs(BindingGenerator.getList(bindings
				.get(IDS_VARIABLE)));
		trace = new TraceResult(operations, states);

	}

	// TODO: Should these methods be saved in some other class?
	private List<String> getStateIDs(final Collection<PrologTerm> terms) {
		final List<String> ids = new ArrayList<String>(terms.size());
		for (final PrologTerm term : terms) {
			ids.add(getStateID(term));
		}
		return ids;
	}

	private String getStateID(final PrologTerm term) {
		final String result;
		if (term.isAtom()) {
			result = PrologTerm.atomicString(term);
		} else {
			result = term.toString();
		}
		return result;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_shortest_trace_to_current_state2");
		pto.printVariable(ACTIONS_VARIABLE);
		pto.printVariable(IDS_VARIABLE).closeTerm();
	}
}
