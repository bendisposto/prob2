package de.prob.animator.command.representation;

import de.prob.ProBException;
import de.prob.animator.command.ICommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Extracts the name of a loaded model from ProB
 * @author joy
 *
 */
public class GetModelNameCommand implements ICommand {

	private static final String NAME = "Name";
	private String name;

	@Override
	public void writeCommand(final IPrologTermOutput pto) throws ProBException {
		pto.openTerm("get_name").printVariable(NAME).closeTerm();

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws ProBException {
		name = PrologTerm.atomicString(bindings.get(NAME));
	}

	public String getName() {
		return name;
	}

}
