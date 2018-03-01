package de.prob.animator.command;


import de.prob.exception.ProBError;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;


public class ConstraintBasedRefinementCheckCommand extends AbstractCommand {
	public enum ResultType {
		VIOLATION_FOUND, NO_VIOLATION_FOUND, INTERRUPTED
	}

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
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ListPrologTerm resultStringTerm = (ListPrologTerm) bindings.get(RESULT_STRINGS_VARIABLE);

		final StringBuilder sb = new StringBuilder();
		for (PrologTerm t : resultStringTerm) {
			sb.append(PrologTerm.atomicString(t));
			sb.append('\n');
		}
		resultsString = sb.toString();
		
		if (resultTerm.hasFunctor("time_out", 0)) {
			this.result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("true", 0)) { // Errors were found
			this.result = ResultType.VIOLATION_FOUND;
			// TODO extract message
		} else if (resultTerm.hasFunctor("false", 0)) { // Errors were not found
			this.result = ResultType.NO_VIOLATION_FOUND;
		} else {
			throw new ProBError("unexpected result from refinement check: " + resultTerm);
		}
	}	

	public String getResultsString() {
		return resultsString;
	}
	
	public ResultType getResult() {
		return result;
	}
}
