/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.animator.command.internal.CheckBooleanPropertyCommand;

//FIXME: Is this actually what the command does?
/**
 * Checks to see if the max number of operations has been reached for a given
 * state
 * 
 * @author joy
 * 
 */
public final class CheckMaxOperationReachedStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "max_operations_reached";

	public CheckMaxOperationReachedStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public boolean maxOperationReached() {
		return super.getResult();
	}

}
