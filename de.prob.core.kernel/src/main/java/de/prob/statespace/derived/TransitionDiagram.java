package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.CalculateTransitionDiagramCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class TransitionDiagram extends AbstractDerivedStateSpace {

	private final String expression;

	public TransitionDiagram(final IStateSpace stateSpace,
			final String expression) {
		super(stateSpace);
		this.expression = expression;
		stateSpace.registerStateSpaceListener(this);
	}

	@Override
	public void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> newOps) {
		CalculateTransitionDiagramCommand cmd = new CalculateTransitionDiagramCommand(
				expression);
		stateSpace.execute(cmd);
		addStates(cmd.getStates());
		List<DerivedOp> nOps = addTransitions(cmd.getOps());
		notifyStateSpaceChange(nOps);
	}

}
