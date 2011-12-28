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
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
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
			final CompoundPrologTerm cpt = (CompoundPrologTerm) op;

			String id = ((IntegerPrologTerm) cpt.getArgument(1)).getValue()
					.toString();

			String name = PrologTerm.atomicString(cpt.getArgument(2));

			String src = "root";
			if (!(cpt.getArgument(3) instanceof CompoundPrologTerm)) {
				src = ((IntegerPrologTerm) cpt.getArgument(3)).getValue()
						.toString();

			}

			String dest = ((IntegerPrologTerm) cpt.getArgument(4)).getValue()
					.toString();

			String args = cpt.getArgument(6).toString();

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
