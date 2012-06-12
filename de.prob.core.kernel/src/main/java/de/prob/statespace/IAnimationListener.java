package de.prob.statespace;

public interface IAnimationListener {
	public void currentStateChanged(StateId fromState, StateId stateId,
			OperationId withOp);
}
