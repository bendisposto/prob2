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
public final class CheckInitialisationStatusCommand extends
CheckBooleanPropertyCommand {

	private static final String IS_INITIALISED_STATE = "initialised";

	public CheckInitialisationStatusCommand(final String stateId) {
		super(IS_INITIALISED_STATE, stateId);
	}

	public boolean isInitialized() {
		return super.getResult();
	}

}
