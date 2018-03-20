package de.prob.animator.command;

import java.util.Collection;
import java.util.stream.Collectors;

import de.prob.animator.domainobjects.StateError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * This command asks ProB if a certain state has errors associated to it.
 * 
 * @author plagge
 */
public class GetStateBasedErrorsCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "get_state_errors";
	private final String stateId;
	private Collection<StateError> stateErrors;

	public GetStateBasedErrorsCommand(final String stateId) {
		this.stateId = stateId;
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		stateErrors = BindingGenerator.getList(bindings, "Errors").stream()
			.map(term -> new StateError(BindingGenerator.getCompoundTerm(term, "error", 3)))
			.collect(Collectors.toList());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtomOrNumber(stateId)
				.printVariable("Errors").closeTerm();
	}

	public Collection<StateError> getResult() {
		return stateErrors;
	}

}
