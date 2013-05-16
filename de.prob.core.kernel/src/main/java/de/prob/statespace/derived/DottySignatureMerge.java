package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.GetDottyForSigMergeCmd;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class DottySignatureMerge extends AbstractDottyGraph {

	private final List<String> disabledEvents;

	public DottySignatureMerge(final IStateSpace space,
			final List<String> disabledEvents) {
		super(space);
		this.disabledEvents = disabledEvents;
		calculate();
	}

	@Override
	protected void calculate() {
		GetDottyForSigMergeCmd cmd = new GetDottyForSigMergeCmd(disabledEvents);
		execute(cmd);
		content = cmd.getContent();
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> newOps) {
		calculate();
		notifyStateSpaceChange(newOps);
	}

}
