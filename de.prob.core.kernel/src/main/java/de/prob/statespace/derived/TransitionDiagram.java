package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.AbstractReduceStateSpaceCmd;
import de.prob.animator.command.CalculateTransitionDiagramCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class TransitionDiagram extends AbstractDerivedStateSpace {

	private static int counter = 0;
	private final String id = "trans-diag" + counter++;
	private final String expression;

	public TransitionDiagram(final IStateSpace stateSpace,
			final String expression, final AbstractReduceStateSpaceCmd cmd) {
		super(stateSpace, cmd);
		this.expression = expression;
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> newOps) {
		CalculateTransitionDiagramCommand cmd = new CalculateTransitionDiagramCommand(
				expression);
		stateSpace.execute(cmd);
		addStates(cmd.getStates());
		List<DerivedOp> nOps = addTransitions(cmd.getOps());
		setNodeColors(cmd.getNodeColors());
		setTransColor(cmd.getTransColor());
		setTransStyle(cmd.getTransStyle());

		notifyStateSpaceChange(nOps);
	}

	@Override
	public String getId() {
		return id;
	}

}
