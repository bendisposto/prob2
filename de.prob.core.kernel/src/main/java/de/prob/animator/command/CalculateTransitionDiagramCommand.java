package de.prob.animator.command;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * @author joy This command corresponds to prolog call
 *         get_transition_diagram(+Expression,-StateSpace)
 */
public class CalculateTransitionDiagramCommand extends
		AbstractReduceStateSpaceCmd {

	private final String expression;

	public CalculateTransitionDiagramCommand(final String e) {
		expression = e;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_transition_diagram");
		pto.printAtom(expression);
		pto.printVariable(SPACE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		// Result term is a list with two arguments [States,Transitions].
		ListPrologTerm list = BindingGenerator.getList(bindings.get(SPACE));

		extractStates(BindingGenerator.getList(list.getArgument(1)));
		extractTransitions(BindingGenerator.getList(list.getArgument(2)));
	}

	public String getExpression() {
		return expression;
	}

}
