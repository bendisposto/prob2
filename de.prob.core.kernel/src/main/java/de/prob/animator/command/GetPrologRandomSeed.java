/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class GetPrologRandomSeed implements ICommand {

	Logger logger = LoggerFactory.getLogger(GetPrologRandomSeed.class);
	
	private RandomSeed randomSeed;

	public RandomSeed getSeed() {
		return randomSeed;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) throws ProBException {
		BigInteger x, y, z, b;
		try {
			x = BindingGenerator.getInteger(bindings.get("X")).getValue();
			y = BindingGenerator.getInteger(bindings.get("Y")).getValue();
			z = BindingGenerator.getInteger(bindings.get("Z")).getValue();
			b = BindingGenerator.getInteger(bindings.get("B")).getValue();
		} catch (ResultParserException e) {
			logger.error(e.getLocalizedMessage());
			throw new ProBException();
		}

		randomSeed = new RandomSeed(x, y, z, b);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_rand").printVariable("X").printVariable("Y")
				.printVariable("Z").printVariable("B").closeTerm();
	}

}
