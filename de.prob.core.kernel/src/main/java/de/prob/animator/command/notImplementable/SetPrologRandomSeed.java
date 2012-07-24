/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command.notImplementable;

import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetPrologRandomSeed implements ICommand {

	private final RandomSeed seed;

	public SetPrologRandomSeed(final RandomSeed seed) {
		this.seed = seed;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// no result, nothing to do
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("set_rand");
		pto.printNumber(seed.getSeedX());
		pto.printNumber(seed.getSeedY());
		pto.printNumber(seed.getSeedZ());
		pto.printNumber(seed.getSeedB());
		pto.closeTerm();
	}
}
