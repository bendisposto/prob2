package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetSvgForVisualizationCommand extends AbstractCommand {
	
	private static final String PROLOG_COMMAND_NAME = "call_dot_command_and_dot";
	
	private static final String STATE_SPACE = "state_as_graph";

	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		pto.printAtomOrNumber(STATE_SPACE);
		pto.emptyList();
		pto.printAtom("svg");
		pto.printAtom("~/Desktop/out.svg");
		pto.closeTerm();
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		
	}

}
