package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadAlloyTermCommand extends AbstractCommand implements IRawCommand {
	private static final String PROLOG_COMMAND_NAME = "load_alloy_spec_from_term";

	private final String term;

	public LoadAlloyTermCommand(final String term) {
		this.term = term.endsWith(".") ? term.substring(0, term.length()-1) : term;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		throw new UnsupportedOperationException("A raw command cannot be written to a IPrologTermOutput");
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		// There are no output variables.
	}

	@Override
	public String getCommand() {
		return PROLOG_COMMAND_NAME + "(" + this.term + ")";
	}
}
