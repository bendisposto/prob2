package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class ApplySignatureMergeCommand implements ICommand {

	public final String SPACE = "StateSpace";

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_signature_merge_state_space");
		pto.printVariable(SPACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		System.out.println(bindings.get(SPACE));
	}

}
