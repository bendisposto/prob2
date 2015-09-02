package de.prob.animator.command;

import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Finds the operations that have a timeout for a specific state
 * 
 * @author joy
 * 
 */
public class GetOperationsWithTimeout extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "op_timeout_occurred";
	private static final String TIMEOUT_VARIABLE = "TO";
	private final String state;
	private List<String> timeouts;

	public GetOperationsWithTimeout(final String state) {
		this.state = state;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		ListPrologTerm list = BindingGenerator.getList(bindings,
				TIMEOUT_VARIABLE);
		timeouts = PrologTerm.atomicStrings(list);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtomOrNumber(state)
				.printVariable(TIMEOUT_VARIABLE).closeTerm();
	}

	public List<String> getTimeouts() {
		return timeouts;
	}
}
