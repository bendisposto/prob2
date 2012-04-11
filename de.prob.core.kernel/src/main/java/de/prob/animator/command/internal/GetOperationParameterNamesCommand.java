package de.prob.animator.command.internal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.command.ICommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetOperationParameterNamesCommand implements ICommand {

	Logger logger = LoggerFactory
			.getLogger(GetOperationParameterNamesCommand.class);

	private static final String PARAMETER_NAMES_VARIABLE = "Names";
	private final String name;
	private List<String> paramNames;

	public GetOperationParameterNamesCommand(final String name) {
		this.name = name;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		try {
			paramNames = PrologTerm.atomicStrings(BindingGenerator
					.getList(bindings.get(PARAMETER_NAMES_VARIABLE)));
		} catch (ResultParserException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
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
