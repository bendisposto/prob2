package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class StartAnimationCommand implements ICommand {
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.printAtom("start_animation");

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}
}