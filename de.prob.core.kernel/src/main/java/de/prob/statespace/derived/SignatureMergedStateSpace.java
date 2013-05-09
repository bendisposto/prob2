package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.AbstractReduceStateSpaceCmd;
import de.prob.animator.command.ApplySignatureMergeCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class SignatureMergedStateSpace extends AbstractDerivedStateSpace {

	public SignatureMergedStateSpace(final IStateSpace stateSpace,
			final AbstractReduceStateSpaceCmd cmd) {
		super(stateSpace, cmd);
		stateSpace.registerStateSpaceListener(this);
	}

	@Override
	public void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> o) {
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand();
		stateSpace.execute(cmd);
		addStates(cmd.getStates());
		List<DerivedOp> newOps = addTransitions(cmd.getOps());
		setNodeColors(cmd.getNodeColors());
		setTransColor(cmd.getTransColor());
		setTransStyle(cmd.getTransStyle());

		notifyStateSpaceChange(newOps);
	}

}
