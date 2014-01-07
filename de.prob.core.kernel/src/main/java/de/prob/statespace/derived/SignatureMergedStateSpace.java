package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.AbstractReduceStateSpaceCmd;
import de.prob.animator.command.ApplySignatureMergeCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class SignatureMergedStateSpace extends AbstractDerivedStateSpace {

	private static int counter = 0;
	private final String id = "sig-merge-ss" + counter++;
	private final List<String> disabledEvents;

	public SignatureMergedStateSpace(final IStateSpace stateSpace,
			final AbstractReduceStateSpaceCmd cmd,
			final List<String> disabledEvents) {
		super(stateSpace, cmd);
		this.disabledEvents = disabledEvents;
	}

	@Override
	public void newTransitions(final List<? extends OpInfo> o) {
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand(
				disabledEvents);
		stateSpace.execute(cmd);
		addStates(cmd.getStates());
		List<DerivedOp> newOps = addTransitions(cmd.getOps());
		setNodeColors(cmd.getNodeColors());
		setTransColor(cmd.getTransColor());
		setTransStyle(cmd.getTransStyle());

		notifyStateSpaceChange(newOps);
	}

	@Override
	public String getId() {
		return id;
	}

}
