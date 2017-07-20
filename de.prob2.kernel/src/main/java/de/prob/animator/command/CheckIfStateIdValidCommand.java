/*
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 */

package de.prob.animator.command;

/**
 * Checks to see if the given state is initialized.
 * 
 * @author joy
 * 
 */
public final class CheckIfStateIdValidCommand extends
		CheckBooleanPropertyCommand {

	private static final String IS_VALID_STATE = "valid_state";

	public CheckIfStateIdValidCommand(final String stateId) {
		super(IS_VALID_STATE, stateId);
	}

	public boolean isValidState() {
		return super.getResult();
	}

}
