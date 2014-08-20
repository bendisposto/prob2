package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class SerializeStateCommand extends AbstractCommand {

	private final String id;
	private String state;

	public SerializeStateCommand(final String id) {
		this.id = id;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		state = PrologTerm.atomicString(bindings.get("State"));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("serialize").printAtomOrNumber(id).printVariable("State")
				.closeTerm();
	}

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

}
