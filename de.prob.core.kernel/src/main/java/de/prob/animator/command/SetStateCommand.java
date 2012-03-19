/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetStateCommand implements ICommand {
	private final String stateId;

	public SetStateCommand(final String stateID) {
		stateId = stateID;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// no results, do nothing
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("setCurrentState").printAtomOrNumber(stateId).closeTerm();
	}

}
