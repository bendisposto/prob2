/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command.notImplemented;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.animator.command.ICommand;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class GetPrologRandomSeed implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetPrologRandomSeed.class);

	private RandomSeed randomSeed;

	public RandomSeed getSeed() {
		return randomSeed;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		BigInteger x, y, z, b;
		x = BindingGenerator.getInteger(bindings.get("X")).getValue();
		y = BindingGenerator.getInteger(bindings.get("Y")).getValue();
		z = BindingGenerator.getInteger(bindings.get("Z")).getValue();
		b = BindingGenerator.getInteger(bindings.get("B")).getValue();

		randomSeed = new RandomSeed(x, y, z, b);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_rand").printVariable("X").printVariable("Y")
				.printVariable("Z").printVariable("B").closeTerm();
	}

}
