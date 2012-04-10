/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetEnabledOperationsCommand implements ICommand {

	private static final String OPERATIONS_VARIABLE = "PLOps";
	private final String id;
	private List<OpInfo> enabledOperations = Collections.emptyList();

	public GetEnabledOperationsCommand(final String id) {
		this.id = id;
	}

	// [op(id,name,src,dest,[Arguments], [ArgsPrettyPrint],[Infos])]
	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		enabledOperations = new ArrayList<OpInfo>();

		final ListPrologTerm prologTerm = (ListPrologTerm) bindings
				.get(OPERATIONS_VARIABLE);
		for (PrologTerm op : prologTerm) {
			String id = op.getArgument(1).getFunctor();
			String name = op.getArgument(2).getFunctor();
			String src = op.getArgument(3).getFunctor();
			String dest = op.getArgument(4).getFunctor();
			String args = op.getArgument(6).toString();
			enabledOperations.add(new OpInfo(id, name, src, dest, args));
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("computeOperationsForState");
		pto.printAtomOrNumber(id);
		pto.printVariable(OPERATIONS_VARIABLE);
		pto.closeTerm();
	}

	public List<OpInfo> getEnabledOperations() {
		return enabledOperations;
	}
}
