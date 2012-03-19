package de.prob.animator.command.internal;


import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetAllOperationsNamesCommand implements ICommand {

	private static final String NAMES_VARIABLE = "Names";
	private ListPrologTerm term;

	public ListPrologTerm getNamesTerm() {
		return term;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		term = (ListPrologTerm) bindings.get(NAMES_VARIABLE);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getAllOperations").printVariable(NAMES_VARIABLE)
				.closeTerm();
	}

}
