/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Calculates the enabled operations for a given state id
 * 
 * @author joy
 * 
 */
public final class GetEnabledOperationsCommand implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetEnabledOperationsCommand.class);

	private static final String OPERATIONS_VARIABLE = "PLOps";
	private final String id;
	private List<OpInfo> enabledOperations = Collections.emptyList();

	public GetEnabledOperationsCommand(final String id) {
		this.id = id;
	}

	// [op(id,name,src,dest,[Arguments], [ArgsPrettyPrint],[Infos])]
	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ResultParserException {
		enabledOperations = new ArrayList<OpInfo>();

		final ListPrologTerm prologTerm = (ListPrologTerm) bindings
				.get(OPERATIONS_VARIABLE);
		for (PrologTerm op : prologTerm) {
			CompoundPrologTerm cpt;
			cpt = BindingGenerator.getCompoundTerm(op, 7);
			enabledOperations.add(new OpInfo(cpt));
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
