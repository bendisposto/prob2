package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadAlloyTermCommand extends AbstractCommand implements IRawCommand {
	private static final String PROLOG_COMMAND_NAME = "load_alloy_spec_from_term";

	private final String term;
	private final String alloyFileName;
		
	public LoadAlloyTermCommand(final String term, final String alloyFileName) {
		this.term = term.endsWith(".") ? term.substring(0, term.length()-1) : term;
		this.alloyFileName = alloyFileName;
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
		// Convert the file name to an escaped Prolog atom
		final IPrologTermOutput pto = new PrologTermStringOutput();
		pto.printAtom(this.alloyFileName);
		return PROLOG_COMMAND_NAME + "(" + this.term + "," + pto + ")";
	}
}
