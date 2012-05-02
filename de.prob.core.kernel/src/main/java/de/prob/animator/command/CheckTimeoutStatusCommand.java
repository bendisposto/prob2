/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.animator.command;

import de.prob.ProBException;
import de.prob.animator.command.internal.CheckBooleanPropertyCommand;

/**
 * Checks to see if a timeout has occured at a given state
 * 
 * @author joy
 * 
 */
public final class CheckTimeoutStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "timeout_occurred";

	public CheckTimeoutStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public boolean isTimeout() throws ProBException {
		return super.getResult();
	}

}
