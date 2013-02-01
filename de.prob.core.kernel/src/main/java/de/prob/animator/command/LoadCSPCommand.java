package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class LoadCSPCommand implements ICommand {

	private final String path;

	public LoadCSPCommand(final String path) {
		this.path = path;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("load_cspm_spec_from_cspm_file");
		pto.printAtom(path);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
	}

}
