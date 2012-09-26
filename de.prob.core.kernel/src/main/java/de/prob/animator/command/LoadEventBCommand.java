package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBCommand implements ICommand, IRawCommand {

	private final String loadcommand;

	public LoadEventBCommand(String loadcommand) {
		this.loadcommand = loadcommand;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		throw new UnsupportedOperationException(
				"This is a raw command. It cannot write to a IPrologTermOutput");
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {

	}

	@Override
	public String getCommand() {
		return loadcommand;
	}

}
