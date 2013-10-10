/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.List;

import de.prob.animator.IAnimator;
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

	public LtlCheckingCommand(final List<IEvalElement> ltlFormula,
			final int max,
			final StartMode mode, final StateId stateid) {
		super(ltlFormula, stateid.getId());
		this.max = max;
		this.mode = mode;
		this.stateid = stateid;
	}

	public static LtlCheckingResult modelCheck(final IAnimator a,
			final List<IEvalElement> fomula,
			final int max, final StartMode mode,
			final StateId stateid) {
		LtlCheckingCommand command = new LtlCheckingCommand(fomula, max, mode,
				stateid);
		a.execute(command);
		return command.getResult();
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
					initPath[i] = new OpInfo((CompoundPrologTerm) opTerm);
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

		result = new LtlCheckingResult(status, atomics, noStructure ? null : structure,
				counterexample, pathType, loopEntry, initPath,
				stateid);
		values.add(result);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		// set the state first
		// TODO: refactor prolog code, we want to use the stateID as a parameter
		pto.openTerm("setCurrentState").printAtomOrNumber(stateid.getId())
		.closeTerm();
		// then call the ltl modelcheck predicate
		pto.openTerm("do_ltl_modelcheck");
		evalElements.get(0).printProlog(pto);
		pto.printNumber(max);
		pto.printAtom(mode.toString());
		pto.printVariable(VARIABLE_NAME_ATOMICS);
		pto.printVariable(VARIABLE_NAME_STRUCTURE);
		pto.printVariable(VARIABLE_NAME_RESULT);
		pto.closeTerm();
	}

}