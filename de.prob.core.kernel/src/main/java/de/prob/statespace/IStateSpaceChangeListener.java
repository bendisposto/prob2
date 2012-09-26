package de.prob.statespace;

public interface IStateSpaceChangeListener {
	public void newTransition(String opName, boolean isDestStateNew);
}
