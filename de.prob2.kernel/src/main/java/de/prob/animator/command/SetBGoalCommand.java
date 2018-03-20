package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class SetBGoalCommand extends AbstractCommand {

	private static final String PROLOG_COMMAND_NAME = "set_goal_for_model_checking";
	private final IEvalElement goal;

	public SetBGoalCommand(final IEvalElement goal) {
		this.goal = goal;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		goal.printProlog(pto);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		// There are no output variables.
	}

}
