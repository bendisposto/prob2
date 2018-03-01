package de.prob.animator.command;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetTransitionDiagramCommand extends AbstractDotDiagramCmd {
	private static final String PROLOG_COMMAND_NAME = "get_transition_diagram";
	private final IEvalElement expression;
	
	public GetTransitionDiagramCommand(final IEvalElement e) {
		expression = e;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(PROLOG_COMMAND_NAME);
		expression.printProlog(pto);
		pto.printVariable(STATE_SPACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(final ISimplifiedROMap<String, PrologTerm> bindings) {
		// Result term is a list with two arguments [States,Transitions].
		ListPrologTerm list = BindingGenerator.getList(bindings.get(STATE_SPACE));
		extractStates(BindingGenerator.getList(list.getArgument(1)));
		extractTransitions(BindingGenerator.getList(list.getArgument(2)));
	}

	public IEvalElement getExpression() {
		return expression;
	}
}
