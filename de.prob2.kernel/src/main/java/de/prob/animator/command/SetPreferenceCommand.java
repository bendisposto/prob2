/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Sets a preference for eclipse.
 * 
 * @author joy
 */
public final class SetPreferenceCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "set_eclipse_preference";
	private final String key;
	private final String value;

	public SetPreferenceCommand(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// no result
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtom(key).printAtom(value)
				.closeTerm();
	}
}
