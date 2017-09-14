package de.prob.animator.command;


import java.util.ArrayList;
import java.util.List;

import de.prob.check.RefinementCheckCounterExample;
import de.prob.exception.ProBError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;


public class ConstraintBasedRefinementCheckCommand extends AbstractCommand implements IStateSpaceModifier {

	public static enum ResultType {
		VIOLATION_FOUND, NO_VIOLATION_FOUND, INTERRUPTED
	};

	private static final String COMMAND_NAME = "refinement_check";
	private static final String RESULT_VARIABLE = "R";
	private static final String RESULT_STRINGS_VARIABLE = "S";
	

	private ResultType result;
	private String resultsString = "";
	
	private final StateSpace s;
	
	private final List<RefinementCheckCounterExample> counterExamples = new ArrayList<RefinementCheckCounterExample>();
	private final List<Transition> newOps = new ArrayList<Transition>();
	
	public ConstraintBasedRefinementCheckCommand(StateSpace s) {
		this.s = s;
	}
		

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.printVariable(RESULT_STRINGS_VARIABLE);
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBError {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;
		final ListPrologTerm resultStringTerm = (ListPrologTerm) bindings
				.get(RESULT_STRINGS_VARIABLE);

		for (PrologTerm t : resultStringTerm) {
			resultsString += PrologTerm.atomicString(t) + "\n";
		}

		if (resultTerm.hasFunctor("time_out", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("true", 0)) { // Errors were found
			result = ResultType.VIOLATION_FOUND;
			// TODO extract message
			CompoundPrologTerm ceTerm = (CompoundPrologTerm) resultTerm;
			extractCounterExample(ceTerm);
		} else if (resultTerm.hasFunctor("false", 0)) { // Errors were not found
			result = ResultType.NO_VIOLATION_FOUND;
		} else
			throw new ProBError("unexpected result from refinement check: " + resultTerm);
		this.result = result;
	}
	
	private void extractCounterExample(final CompoundPrologTerm term) {
			final String eventName = PrologTerm.atomicString(term
					.getArgument(1));
			final Transition step1 = Transition
					.createTransitionFromCompoundPrologTerm(
							s,
							BindingGenerator.getCompoundTerm(
									term.getArgument(2), 4));
			final Transition step2 = Transition
					.createTransitionFromCompoundPrologTerm(
							s,
							BindingGenerator.getCompoundTerm(
									term.getArgument(3), 4));
			final RefinementCheckCounterExample ce = new RefinementCheckCounterExample(
					eventName, step1, step2);
			newOps.add(step1);
			newOps.add(step2);
			counterExamples.add(ce);
	}
	

	public String getResultsString() {
		return resultsString;
	}
	
	public ResultType getResult() {
		return result;
	}
	

	@Override
	public List<Transition> getNewTransitions() {
		return newOps;
	}
	
	public List<RefinementCheckCounterExample> getCounterExamples() {
		return counterExamples;
	}

}
