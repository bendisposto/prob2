package de.prob.model.representation;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadBProjectFromStringCommand implements ICommand {

	public LoadBProjectFromStringCommand(final String input) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		throw new UnsupportedOperationException("Not yet implemented");

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		throw new UnsupportedOperationException("Not yet implemented");

	}

}
