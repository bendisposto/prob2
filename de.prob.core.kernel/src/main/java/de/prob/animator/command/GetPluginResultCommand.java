/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetPluginResultCommand extends AbstractCommand {

	private final String resultID;
	private CompoundPrologTerm result;

	public GetPluginResultCommand(final String resultID) {
		this.resultID = resultID;
	}

	public CompoundPrologTerm getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
			result = BindingGenerator.getCompoundTerm(bindings.get("Bindings"), 1);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_plugin_output").printAtomOrNumber(resultID)
				.printVariable("Bindings").closeTerm();
	}

}
