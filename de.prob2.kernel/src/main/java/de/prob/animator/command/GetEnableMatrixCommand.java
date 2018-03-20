package de.prob.animator.command;

import java.util.HashMap;
import java.util.Map;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetEnableMatrixCommand extends AbstractCommand {
	public static class EventPair {
		private final String first;
		private final String second;

		public EventPair(String first, String second) {
			this.first = first;
			this.second = second;
		}

		public String getFirst() {
			return first;
		}

		public String getSecond() {
			return second;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() != getClass()) {
				return false;
			}
			EventPair that = (EventPair) obj;
			return this.first.equals(that.first) && this.second.equals(that.second);
		}

		@Override
		public int hashCode() {
			return first.hashCode() + 37 * second.hashCode();
		}
	}

	public static class EnableMatixEntry {
		public final String enable;
		public final String keepEnabled;
		public final String disable;
		public final String keepDisabled;

		public EnableMatixEntry(PrologTerm term) {
			enable = term.getArgument(1).getFunctor();
			keepEnabled = term.getArgument(2).getFunctor();
			disable = term.getArgument(3).getFunctor();
			keepDisabled = term.getArgument(4).getFunctor();
		}
		
		@Override
		public String toString() {
			return String.format("Enable: %s, Keep enabled: %s, Disable: %s, Keep disabled: %s %n", enable, keepEnabled,disable, keepDisabled);
		}
	}

	private static final String PROLOG_COMMAND_NAME = "get_enable_matrix";
	private static final String MATRIX_VAR = "Matrix";

	private final EventPair[] pairs;
	private Map<EventPair, EnableMatixEntry> matrix = new HashMap<>();

	public GetEnableMatrixCommand(EventPair... eventPairs) {
		this.pairs = eventPairs;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.openList();
		for (final EventPair pair : pairs) {
			pto.openTerm("pair");
			pto.printAtom(pair.getFirst());
			pto.printAtom(pair.getSecond());
			pto.closeTerm();
		}
		pto.closeList();
		pto.printVariable(MATRIX_VAR);
		pto.closeTerm();
	}

	// yes('.'(=('Matrix','.'(enable_rel(new,del,enable_edges(ok,ok,false,false)),[])),[]))
	// enable_edges(Enable,KeepEnabled,Disable,KeepDisabled)
	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm elements = (ListPrologTerm) bindings.get(MATRIX_VAR);
		matrix.clear();
		for (PrologTerm term : elements) {
			EventPair key = new EventPair(term.getArgument(1).getFunctor(), term.getArgument(2).getFunctor());
			matrix.put(key, new EnableMatixEntry(term.getArgument(3)));
		}
	}

	public EnableMatixEntry getEnableInfo(EventPair key) {
		return matrix.get(key);
	}

}
