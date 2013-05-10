package de.prob.animator.command;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ApplySignatureMergeCommand extends AbstractReduceStateSpaceCmd {

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_signature_merge_state_space");
		pto.printVariable(SPACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// Result term is a list with two arguments [States,Transitions].
		ListPrologTerm list = BindingGenerator.getList(bindings.get(SPACE));

		extractStates(BindingGenerator.getList(list.getArgument(1)));
		extractTransitions(BindingGenerator.getList(list.getArgument(2)));
	}

}
