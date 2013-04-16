package de.prob.statespace;

import java.util.List;

import de.prob.animator.domainobjects.OpInfo;

public interface IStatesCalculatedListener {
	public void newTransitions(StateSpace s, List<OpInfo> newOps);
}
