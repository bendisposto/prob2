/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.ProBException;
import de.prob.animator.IAnimator;

public final class CheckInvariantStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "invariantKO";

	public CheckInvariantStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public boolean isInvariantViolated(final IAnimator a, final String stateId)
			throws ProBException {
		return super.getResult();
	}

}
