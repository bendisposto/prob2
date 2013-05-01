package de.prob.statespace.derived;

import java.util.List;

import de.prob.animator.command.ApplySignatureMergeCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.OpInfo;

public class SignatureMergedStateSpace extends AbstractDerivedStateSpace {

	public SignatureMergedStateSpace(final IStateSpace stateSpace) {
		super(stateSpace);
		stateSpace.registerStateSpaceListener(this);
	}

	@Override
	public void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> o) {
		ApplySignatureMergeCommand cmd = new ApplySignatureMergeCommand();
		stateSpace.execute(cmd);
		addStates(cmd.getStates());
		List<DerivedOp> newOps = addTransitions(cmd.getOps());
		notifyStateSpaceChange(newOps);
	}

}
