package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetPreferenceCommand extends AbstractCommand {
	private static final String PROLOG_COMMAND_NAME = "get_eclipse_preference";
	private static final String VALUE = "Value";

	private final String key;
	private String resultV;

	public GetPreferenceCommand(final String key) {
		this.key = key;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME).printAtom(key).printVariable(VALUE).closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		resultV = bindings.get(VALUE).getFunctor();
	}

	public String getValue() {
		return resultV;
	}

	public String getKey() {
		return key;
	}

}
