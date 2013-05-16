/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.core.editor.command.RemoveEventCommand;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.event.Event;

public class RemoveEventAction extends SelectionAction {

	private Event  event;
	private BControl control;
	
	public RemoveEventAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected boolean calculateEnabled() {
		if (event != null && control != null)
			return true;
		return false;
	}

	public void run() {
		execute(createRemoveEventCommand());
	}

	public RemoveEventCommand createRemoveEventCommand() {
		return new RemoveEventCommand(this.event, this.control);
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

}
