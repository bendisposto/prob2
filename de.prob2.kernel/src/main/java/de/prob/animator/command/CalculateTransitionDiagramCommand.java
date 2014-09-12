package de.prob.animator.command;

import de.prob.animator.domainobjects.EvalElementType;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.statespace.StateSpace;

/**
 * @author joy This command corresponds to prolog call
 *         get_transition_diagram(+Expression,-StateSpace)
 */
public class CalculateTransitionDiagramCommand extends
		AbstractReduceStateSpaceCmd {

	private final IEvalElement expression;

	public CalculateTransitionDiagramCommand(final StateSpace s,
			final IEvalElement expression) {
		super(s);
		this.expression = expression;
		if (!expression.getKind().equals(EvalElementType.EXPRESSION.toString())) {
			throw new IllegalArgumentException(
					"Expected formula of type expression. Formula found was: "
							+ expression.getCode());
		}
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_transition_diagram");
		expression.printProlog(pto);
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
		return expression.getCode();
	}

}
