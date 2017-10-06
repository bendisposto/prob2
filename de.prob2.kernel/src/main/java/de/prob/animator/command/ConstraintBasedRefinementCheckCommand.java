package de.prob.animator.command;


import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;


public class ConstraintBasedRefinementCheckCommand extends AbstractCommand {

	public static enum ResultType {
		VIOLATION_FOUND, NO_VIOLATION_FOUND, INTERRUPTED
	};

	private static final String COMMAND_NAME = "refinement_check";
	private static final String RESULT_VARIABLE = "R";
	private static final String RESULT_STRINGS_VARIABLE = "S";
	

	private ResultType result;
	private String resultsString = "";
	
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
		} else if (resultTerm.hasFunctor("false", 0)) { // Errors were not found
			result = ResultType.NO_VIOLATION_FOUND;
		} else
			throw new ProBError("unexpected result from refinement check: " + resultTerm);
		this.result = result;
	}	

	public String getResultsString() {
		return resultsString;
	}
	
	public ResultType getResult() {
		return result;
	}
	
}
