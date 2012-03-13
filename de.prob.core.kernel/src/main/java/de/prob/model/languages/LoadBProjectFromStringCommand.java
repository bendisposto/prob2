package de.prob.model.languages;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadBProjectFromStringCommand implements ICommand {

	private final String input;
	private final String name;

	public LoadBProjectFromStringCommand(final String input, final String name) {
		this.input = input;
		this.name = name;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		// TODO Auto-generated method stub

	}

}
