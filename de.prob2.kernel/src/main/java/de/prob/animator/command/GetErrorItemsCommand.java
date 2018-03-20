package de.prob.animator.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.ErrorItem;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetErrorItemsCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_error_messages_with_span_info";
	private static final String ERRORS_VARIABLE = "Errors";
	private static final String WARNINGS_ONLY_VARIABLE = "WarningsOnly";

	private boolean warningsOnly;
	private List<ErrorItem> errors = Collections.emptyList();

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable(WARNINGS_ONLY_VARIABLE).printVariable(ERRORS_VARIABLE)
			.closeTerm();
	}

	private static ErrorItem.Location convertLocationTerm(final PrologTerm location) {
		if (!location.hasFunctor("error_span", 5)) {
			throw new IllegalArgumentException(String.format(
				"Error locations list should contain terms of form " +
				"error_span(Filename,StartLine,StartCol,EndLine,EndCol), " +
				"but found term %s with arity %d",
				location.getFunctor(), location.getArity()
			));
		}
		
		final String filename = PrologTerm.atomicString(location.getArgument(1));
		final int startLine = ((IntegerPrologTerm)location.getArgument(2)).getValue().intValueExact();
		final int startColumn = ((IntegerPrologTerm)location.getArgument(3)).getValue().intValueExact();
		final int endLine = ((IntegerPrologTerm)location.getArgument(4)).getValue().intValueExact();
		final int endColumn = ((IntegerPrologTerm)location.getArgument(5)).getValue().intValueExact();
		
		return new ErrorItem.Location(filename, startLine, startColumn, endLine, endColumn);
	}

	private static ErrorItem convertErrorTerm(final PrologTerm error) {
		if (!error.hasFunctor("error", 3)) {
			throw new IllegalArgumentException(String.format(
				"Errors list should contain terms of form " +
				"error(Msg,Type,Locations), but found term %s with arity %d",
				error.getFunctor(), error.getArity()
			));
		}
		
		final String message = PrologTerm.atomicString(error.getArgument(1));
		
		final String typeName = PrologTerm.atomicString(error.getArgument(2));
		final ErrorItem.Type type;
		switch (typeName) {
			case "warning":
				type = ErrorItem.Type.WARNING;
				break;
			
			case "error":
				type = ErrorItem.Type.ERROR;
				break;
			
			case "internal_error":
				type = ErrorItem.Type.INTERNAL_ERROR;
				break;
			
			default:
				throw new IllegalArgumentException("Unknown error type: " + typeName);
		}
		
		final List<ErrorItem.Location> locations = ((ListPrologTerm)error.getArgument(3)).stream()
			.map(GetErrorItemsCommand::convertLocationTerm)
			.collect(Collectors.toList());
		
		return new ErrorItem(message, type, locations);
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		warningsOnly = "true".equals(bindings.get(WARNINGS_ONLY_VARIABLE).getFunctor());
		errors = ((ListPrologTerm)bindings.get(ERRORS_VARIABLE)).stream()
			.map(GetErrorItemsCommand::convertErrorTerm)
			.collect(Collectors.toList());
	}

	public boolean onlyWarningsOccurred() {
		return warningsOnly;
	}

	public List<ErrorItem> getErrors() {
		return errors;
	}
}
