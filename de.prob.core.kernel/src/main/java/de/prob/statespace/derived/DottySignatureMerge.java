package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.GetDottyForSigMergeCmd;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class DottySignatureMerge extends AbstractDottyGraph {

	public DottySignatureMerge(final IStateSpace space) {
		super(space);
		calculate();
	}

	@Override
	protected void calculate() {
		GetDottyForSigMergeCmd cmd = new GetDottyForSigMergeCmd();
		execute(cmd);
		content = cmd.getContent();
	}

	@Override
	public void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> newOps) {
		calculate();
		notifyStateSpaceChange(newOps);
	}

}
