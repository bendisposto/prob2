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

	private static final String PROLOG_COMMAND_NAME = "get_enable_matrix";
	private static final String MATRIX = "Matrix";
	private final EventPair[] pairs;
	private Map<EventPair, String> matrix = new HashMap<>();

	public GetEnableMatrixCommand(EventPair[] eventPairs) {
		this.pairs = eventPairs;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.openList();
		for (int i = 0; i < pairs.length; i++) {
			pto.openTerm("pair");
			pto.printAtom(pairs[i].getFirst());
			pto.printAtom(pairs[i].getSecond());
			pto.closeTerm();
		}
		pto.closeList();
		pto.printVariable(MATRIX);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm elements = (ListPrologTerm) bindings.get(MATRIX);
		matrix.clear();
		for (PrologTerm term : elements) {
			EventPair key = new EventPair(term.getArgument(1).getFunctor(), term.getArgument(2).getFunctor());
			String value = term.getArgument(3).getFunctor();
			matrix.put(key, value);
		}
	}

	public String getEnableInfo(EventPair key) {
		return matrix.get(key);
	}

}
