package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class DeserializeStateCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "deserialize";
	private String id;
	private final String state;

	public DeserializeStateCommand(final String state) {
		this.state = state;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		id = bindings.get("Id").toString();
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printVariable("Id").printAtom(state)
				.closeTerm();
	}

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

}
