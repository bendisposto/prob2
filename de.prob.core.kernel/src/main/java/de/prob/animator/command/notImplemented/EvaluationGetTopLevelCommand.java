package de.prob.animator.command.notImplemented;

import de.prob.animator.command.ICommand;
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
public class EvaluationGetTopLevelCommand implements ICommand {

	public final String TOPS = "Tops";
	
	@Override
	public void writeCommand(IPrologTermOutput pto) {
		pto.openTerm("evaluation_get_top_level");
		pto.printVariable(TOPS);
		pto.closeTerm();

	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings) {
		PrologTerm prologTerm = bindings.get(TOPS);

	}

}
