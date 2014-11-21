package de.prob.animator.command;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetInternalRepresentationPrettyPrintCommand extends AbstractCommand {
	public static final String VARIABLE = "PP";
	private String pp;

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		pp = PrologTerm.atomicString(bindings
				.get(VARIABLE));
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_pretty_print").printVariable(VARIABLE)
				.closeTerm();
	}

	public String getPrettyPrint() {
		return pp;
	}
}