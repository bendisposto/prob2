package de.prob.animator.command.internal;

import java.util.List;

import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetOperationParameterNames implements ICommand {

	private static final String PARAMETER_NAMES_VARIABLE = "Names";
	private final String name;
	private List<String> paramNames;

	public GetOperationParameterNames(final String name) {
		this.name = name;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings){
		paramNames = PrologTerm.atomicStrings((ListPrologTerm) bindings
				.get(PARAMETER_NAMES_VARIABLE));
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getOperationParameterNames");
		pto.printAtom(name);
		pto.printVariable(PARAMETER_NAMES_VARIABLE);
		pto.closeTerm();
	}

	public List<String> getParameterNames() {
		return paramNames;
	}

}
