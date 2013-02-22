package de.prob.statespace;

import java.util.List;

import de.prob.animator.domainobjects.OpInfo;

public interface IStateSpaceChangeListener {
	public void newTransitions(List<OpInfo> ops);
}
