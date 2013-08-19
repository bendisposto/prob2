package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBCommand extends AbstractCommand implements IRawCommand {

	private final String loadcommand;

	public LoadEventBCommand(final String loadcommand) {
		this.loadcommand = loadcommand;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		throw new UnsupportedOperationException(
				"This is a raw command. It cannot write to a IPrologTermOutput");
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {

	}

	@Override
	public String getCommand() {
		return loadcommand;
	}

	@Override
	public String[] getVariables() {
		return new String[] {};
	}

}
