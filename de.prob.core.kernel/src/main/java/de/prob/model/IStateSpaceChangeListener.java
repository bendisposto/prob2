package de.prob.model;

public interface IStateSpaceChangeListener {
	public void newTransition(String opName, boolean isDestStateNew);
}
