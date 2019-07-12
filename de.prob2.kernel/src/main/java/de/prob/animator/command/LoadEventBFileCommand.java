package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadEventBFileCommand extends AbstractCommand implements
		IRawCommand {

	private final String loadcommand;

	public LoadEventBFileCommand(final String loadcommand) {
		this.loadcommand = loadcommand;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		throw new UnsupportedOperationException(
				"This is a raw command. It cannot write to a IPrologTermOutput");
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		// There are no output variables.
	}

	@Override
	public String getCommand() {
		return loadcommand;
	}

}
