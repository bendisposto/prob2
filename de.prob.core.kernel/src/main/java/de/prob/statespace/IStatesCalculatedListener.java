package de.prob.statespace;

import java.util.List;

import de.prob.animator.domainobjects.OpInfo;

public interface IStatesCalculatedListener {
	public void newTransitions(StateSpaceGraph s, List<OpInfo> newOps);
}
