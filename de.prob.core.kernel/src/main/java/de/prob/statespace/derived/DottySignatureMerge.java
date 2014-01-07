package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.GetDottyForSigMergeCmd;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class DottySignatureMerge extends AbstractDottyGraph {

	private static int counter = 0;
	private final String id = "dotty-sig-merge" + counter++;

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
