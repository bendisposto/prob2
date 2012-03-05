package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetErrorsCommand implements ICommand {
	public static final String ERRORS_VARIABLE = "Errors";
	private List<String> errors;

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		errors = PrologTerm.atomicStrings((ListPrologTerm) bindings
				.get(ERRORS_VARIABLE));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		errors = Collections.emptyList();
		pto.openTerm("getErrorMessages").printVariable(ERRORS_VARIABLE)
				.closeTerm();
	}

	public List<String> getErrors() {
		return errors;
	}
}