package de.prob.model.representation;

import de.prob.statespace.StateSpace;

public abstract class AbstractModel {

	protected StateSpace statespace;

	public StateSpace getStatespace() {
		return statespace;
	}

}
