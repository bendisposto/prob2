/**
 * 
 */
package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.prob.animator.domainobjects.StateError;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
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
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		final List<StateError> errors;
		ListPrologTerm list;
		list = BindingGenerator.getList(bindings, "Errors");

		if (list.isEmpty()) {
			errors = Collections.emptyList();
		} else {
			errors = new ArrayList<StateError>();
			for (PrologTerm term : list) {
				CompoundPrologTerm compoundTerm;
				compoundTerm = BindingGenerator.getCompoundTerm(term, "error",
						3);
				errors.add(new StateError(compoundTerm));
			}
		}
		stateErrors = errors;
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
