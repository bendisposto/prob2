/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.event.Event;

public class RemoveEventCommand extends Command {

	private BControl control;
	
	private Event event;
	
	public RemoveEventCommand(Event event, BControl control) {
		this.event = event;
		this.control = control;
	}

	public void execute() {
		redo();
	}

	public boolean canExecute() {
		return event != null && control != null;
	}

	public void undo() {
		control.addEvent(event);
	}

	public void redo() {
		control.removeEvent(event);
	}
	
	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return this.control;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	

}
