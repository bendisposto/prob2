package de.prob.animator.command.notImplemented;

import de.prob.animator.command.AbstractCommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * This command retrieves the IDs of the top-level expressions and their labels
 * and the IDs of their children.
 * 
 * @see EvaluationExpandCommand
 * @see EvaluationGetValuesCommand
 * @author plagge
 */
public class EvaluationGetTopLevelCommand extends AbstractCommand {

	public final String TOPS = "Tops";

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_get_top_level");
		pto.printVariable(TOPS);
		pto.closeTerm();

	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TOPS);

	}

}
