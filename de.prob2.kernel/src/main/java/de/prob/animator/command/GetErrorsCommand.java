package de.prob.animator.command;

import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Gets a list of the errors from ProB
 * 
 * @author joy
 * 
 */
public class GetErrorsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_error_messages";
	public static final String ERRORS_VARIABLE = "Errors";
	public static final String WARNINGS_ONLY_VARIABLE = "WarningsOnly";
	private List<String> errors;
	private boolean warningsOnly;

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		errors = PrologTerm.atomicStrings((ListPrologTerm) bindings
				.get(ERRORS_VARIABLE));
		warningsOnly = "true".equals(bindings.get(WARNINGS_ONLY_VARIABLE).getFunctor());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		errors = Collections.emptyList();
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(WARNINGS_ONLY_VARIABLE).printVariable(ERRORS_VARIABLE)
				.closeTerm();
	}

	public List<String> getErrors() {
		return errors;
	}
	
	public boolean onlyWarningsOccurred() {
		return warningsOnly;
	}
}