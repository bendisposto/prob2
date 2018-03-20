/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Transition;

/**
 * Calculates the enabled operations for a given state id.
 * 
 * @author joy
 */
public final class GetEnabledOperationsCommand extends AbstractCommand
		implements IStateSpaceModifier {
	private static final String PROLOG_COMMAND_NAME = "compute_operations_for_state";
	private static final String OPERATIONS_VARIABLE = "PLOps";

	private final String id;
	private List<Transition> enabledOperations = Collections.emptyList();

	private final StateSpace s;

	public GetEnabledOperationsCommand(final StateSpace s, final String id) {
		this.s = s;
		this.id = id;
	}

	// [op(id,name,src,dest,[Arguments], [ArgsPrettyPrint],[Infos])]
	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		enabledOperations = ((ListPrologTerm)bindings.get(OPERATIONS_VARIABLE)).stream()
			.map(op -> Transition.createTransitionFromCompoundPrologTerm(
				s, BindingGenerator.getCompoundTerm(op, 4)))
			.collect(Collectors.toList());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(id);
		pto.printVariable(OPERATIONS_VARIABLE);
		pto.closeTerm();
	}

	public List<Transition> getEnabledOperations() {
		return enabledOperations;
	}

	@Override
	public List<Transition> getNewTransitions() {
		return this.getEnabledOperations();
	}
}
