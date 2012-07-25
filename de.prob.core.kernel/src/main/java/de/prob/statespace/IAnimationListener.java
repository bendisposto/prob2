package de.prob.statespace;

import de.prob.animator.domainobjects.OpInfo;

public interface IAnimationListener {
	public void currentStateChanged(StateId fromState, StateId stateId,
			OpInfo withOp);
}
