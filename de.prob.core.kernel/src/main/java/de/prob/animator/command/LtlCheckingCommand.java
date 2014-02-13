/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.animator.domainobjects.LtlCheckingResult;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

/**
 * @ Andriy: Das ist jetzt deine Baustelle :)
 */
public final class LtlCheckingCommand extends EvaluationCommand {

	private static final String VARIABLE_NAME_ATOMICS = "A";
	private static final String VARIABLE_NAME_STRUCTURE = "S";
	private static final String VARIABLE_NAME_RESULT = "R";

	public static enum StartMode {
		init, // checks formula in initialisation state(s)
		starthere, // checks formula in current state
		checkhere /*
				 * start in initialisation state(s) and check the formula in
				 * current state
				 */
	};

	public static enum Status {
		incomplete(false), ok(true), counterexample(true), nostart(true), typeerror(
				true);
		private final boolean abort;

		private Status(final boolean abort) {
			this.abort = abort;
		}

		public boolean isAbort() {
			return abort;
		}
	}

	public static enum PathType {
		INFINITE, FINITE, REDUCED
	};

	private final int max;
	private final StartMode mode;
	private LtlCheckingResult result;
	private final StateId stateid;

	public LtlCheckingCommand(final IEvalElement ltlFormula, final int max,
			final StartMode mode, final StateId stateid) {
		super(ltlFormula, stateid.getId());
		this.max = max;
		this.mode = mode;
		this.stateid = stateid;
	}

	public LtlCheckingResult getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		CompoundPrologTerm term = (CompoundPrologTerm) bindings
				.get(VARIABLE_NAME_RESULT);

		final Status status = Enum.valueOf(Status.class, term.getFunctor());

		final ListPrologTerm counterexample;
		final PathType pathType;
		final int loopEntry;
		final OpInfo[] initPath;
		if (term.hasFunctor("counterexample", 3)) {
			counterexample = (ListPrologTerm) term.getArgument(1);
			CompoundPrologTerm loopStatus = (CompoundPrologTerm) term
					.getArgument(2);
			if (loopStatus.hasFunctor("no_loop", 0)) {
				pathType = PathType.REDUCED;
				loopEntry = -1;
			} else if (loopStatus.hasFunctor("deadlock", 0)) {
				pathType = PathType.FINITE;
				loopEntry = -1;
			} else if (loopStatus.hasFunctor("loop", 1)) {
				pathType = PathType.INFINITE;
				loopEntry = ((IntegerPrologTerm) loopStatus.getArgument(1))
						.getValue().intValue();
			} else {
				throw new RuntimeException(
						"LTL model check returned unexpected loop status: "
								+ loopStatus);
			}
			final ListPrologTerm operationIds = (ListPrologTerm) term
					.getArgument(3);
			initPath = new OpInfo[operationIds.size()];
			int i = 0;
			for (final PrologTerm opTerm : operationIds) {
				if (opTerm instanceof CompoundPrologTerm) {
					// initPath[i] = new OpInfo((CompoundPrologTerm) opTerm);
				} else {
					throw new ClassCastException(
							"LTL model check returned invalid result");
				}
				i++;
			}
		} else {
			counterexample = null;
			pathType = null;
			loopEntry = -1;
			initPath = null;
		}

		final ListPrologTerm atomics = (ListPrologTerm) bindings
				.get(VARIABLE_NAME_ATOMICS);
		final PrologTerm structure = bindings.get(VARIABLE_NAME_STRUCTURE);
		final boolean noStructure = (structure instanceof ListPrologTerm)
				&& ((ListPrologTerm) structure).isEmpty();

		value = new LtlCheckingResult(evalElement, status, atomics,
				noStructure ? null : structure, counterexample, pathType,
				loopEntry, initPath, stateid);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_ltl_modelcheck");
		pto.printAtomOrNumber(stateid.getId());
		evalElement.printProlog(pto);
		pto.printNumber(max);
		pto.printAtom(mode.toString());
		pto.printVariable(VARIABLE_NAME_ATOMICS);
		pto.printVariable(VARIABLE_NAME_STRUCTURE);
		pto.printVariable(VARIABLE_NAME_RESULT);
		pto.closeTerm();
	}

	public static LtlCheckingResult modelCheck(final StateSpace s,
			final IEvalElement formula, final int max,
			final StartMode startMode, final StateId stateId) {
		LtlCheckingCommand cmd = new LtlCheckingCommand(formula, max,
				startMode, stateId);
		s.execute(cmd);
		return cmd.getResult();
	}

}