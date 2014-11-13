package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.State;

public class GetBStateCommand extends AbstractCommand {

	private static final String STATE = "State";
	private final State id;
	private String stateRep;

	public GetBStateCommand(final State id) {
		this.id = id;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_b_state");
		pto.printAtomOrNumber(id.getId());
		pto.printVariable(STATE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		stateRep = bindings.get(STATE).getFunctor();
	}

	public String getState() {
		return stateRep;
	}

}
