/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.check;

import java.util.ArrayList;
import java.util.List;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ModelCheckingResult {

	private final Result result;
	private final List<PrologTerm> arguments = new ArrayList<PrologTerm>();

	public static enum Result {
		ok(true), ok_not_all_nodes_considered(true), deadlock(true), invariant_violation(
				true), assertion_violation(true), not_yet_finished(false), state_error(
				true), well_definedness_error(true), general_error(true);
		// I assume true means we can stop the model checking
		private final boolean abort;

		private Result(final boolean abort) {
			this.abort = abort;
		}

		public boolean isAbort() {
			return abort;
		}
	}

	public ModelCheckingResult(final CompoundPrologTerm term) {
		result = Enum.valueOf(Result.class, term.getFunctor());
		for (int i = 1; i <= term.getArity(); i++) {
			arguments.add(term.getArgument(i));
		}
	}

	public PrologTerm getArgument(final int i) {
		return arguments.get(i);
	}

	public Result getResult() {
		return result;
	}

	public boolean isAbort() {
		return result.isAbort();
	}

	@Override
	public String toString() {
		return result.name();
	}
}
