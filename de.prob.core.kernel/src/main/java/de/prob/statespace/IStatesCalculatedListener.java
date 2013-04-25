package de.prob.statespace;

import java.util.List;

public interface IStatesCalculatedListener {
	public void newTransitions(IStateSpace s, List<? extends OpInfo> newOps);
}
