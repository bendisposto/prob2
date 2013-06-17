package de.prob.statespace;

import java.util.List;

import de.prob.animator.IAnimator;

public interface IStateSpace extends IAnimator {

	public void notifyStateSpaceChange(final List<? extends OpInfo> newOps);

	public void registerStateSpaceListener(final IStatesCalculatedListener l);

	public void deregisterStateSpaceListener(final IStatesCalculatedListener l);
}
