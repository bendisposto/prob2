/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.LTL;
import de.prob.check.IModelCheckingResult;
import de.prob.check.LTLCounterExample;
import de.prob.check.LTLError;
import de.prob.check.LTLNotYetFinished;
import de.prob.check.LTLOk;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

public final class LtlCheckingCommand extends EvaluationCommand implements
		IStateSpaceModifier {
	private static final String PROLOG_COMMAND_NAME = "prob2_do_ltl_modelcheck";
	private static final String VARIABLE_NAME_RESULT = "R";
	private static final String VARIABLE_NAME_ERRORS = "Errors";

	public enum PathType {
		INFINITE, FINITE, REDUCED
	}

	private final int max;
	private IModelCheckingResult result;
	private final LTL ltlFormula;
	private final StateSpace s;

	public LtlCheckingCommand(final StateSpace s, final LTL ltlFormula, final int max) {
		super(ltlFormula, null);
		this.s = s;
		this.ltlFormula = ltlFormula;
		this.max = max;
	}

	public IModelCheckingResult getResult() {
		return result;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm term = bindings.get(VARIABLE_NAME_RESULT);

		if (term.hasFunctor("ok", 0)) {
			LTLOk res = new LTLOk(ltlFormula);
			result = res;
			value = res;
		} else if (term.hasFunctor("nostart", 0)) {
			LTLError res = new LTLError(ltlFormula,
					"Could not find initialisation. Try to animating the model.");
			result = res;
			value = res;
		} else if (term.hasFunctor("typeerror", 0)) {
			LTLError res = new LTLError(ltlFormula,
					"Type error discovered in formula");
			result = res;
			value = res;
		} else if (term.hasFunctor("incomplete", 0)) {
			LTLNotYetFinished res = new LTLNotYetFinished(ltlFormula);
			result = res;
			value = res;
		} else if (term.hasFunctor("counterexample", 3)) {
			CompoundPrologTerm cpt = BindingGenerator.getCompoundTerm(term, 3);
			List<Transition> counterExample = BindingGenerator.getList(cpt.getArgument(1)).stream()
				.filter(pt -> !pt.hasFunctor("none", 0))
				.map(pt -> Transition.createTransitionFromCompoundPrologTerm(
					s, BindingGenerator.getCompoundTerm(pt, 4)))
				.collect(Collectors.toList());
			
			PathType pathType;
			int loopEntry;
			PrologTerm loopStatus = cpt.getArgument(2);
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
				
				throw new UnexpectedLoopStatusException(
						"LTL model check returned unexpected loop status: "
								+ loopStatus);
			}
			
			List<Transition> pathToCE = BindingGenerator.getList(cpt.getArgument(3)).stream()
				.map(pt -> Transition.createTransitionFromCompoundPrologTerm(
					s, BindingGenerator.getCompoundTerm(pt, 4)))
				.collect(Collectors.toList());
			
			LTLCounterExample res = new LTLCounterExample(ltlFormula, pathToCE, counterExample, loopEntry, pathType);
			result = res;
			value = res;
		} else {
			throw new UnknownLtlResult("Unknown result from LTL checking: " + term);
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		evalElement.printProlog(pto);
		pto.printNumber(max);
		pto.printVariable(VARIABLE_NAME_RESULT);
		pto.printVariable(VARIABLE_NAME_ERRORS);
		pto.closeTerm();
	}

	public static IModelCheckingResult modelCheck(final StateSpace s, final LTL formula, final int max) {
		LtlCheckingCommand cmd = new LtlCheckingCommand(s, formula, max);
		s.execute(cmd);
		return cmd.getResult();
	}

	@Override
	public List<Transition> getNewTransitions() {
		if (result instanceof LTLCounterExample) {
			return ((LTLCounterExample) result).getOpList();
		} else {
			return Collections.emptyList();
		}
	}
}
