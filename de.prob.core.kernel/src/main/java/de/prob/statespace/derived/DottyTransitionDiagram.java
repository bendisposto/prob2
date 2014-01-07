package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.GetDottyForTransitionDiagramCmd;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class DottyTransitionDiagram extends AbstractDottyGraph {

	private static int counter = 0;
	private final String id = "dotty-trans-diag" + counter++;
	private final String expression;

	public DottyTransitionDiagram(final IStateSpace space,
			final String expression) {
		super(space);
		this.expression = expression;
		calculate();
	}

	@Override
	protected void calculate() {
		GetDottyForTransitionDiagramCmd cmd = new GetDottyForTransitionDiagramCmd(
				expression);
		execute(cmd);
		content = cmd.getContent();
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> newOps) {
		if (!newOps.isEmpty()) {
			calculate();
			notifyStateSpaceChange(newOps);
		}
	}

	@Override
	public String getId() {
		return id;
	}

}
