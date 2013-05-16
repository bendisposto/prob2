/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.event.Event;

public class PasteEventCommand extends Command {

	private CopyPasteHelper cHelper;

	private HashMap<Event, Event> mapping = new HashMap<Event, Event>();

	private BControl control;

	@Override
	public boolean canExecute() {
		cHelper = (CopyPasteHelper) Clipboard.getDefault().getContents();
		if (cHelper == null || control == null)
			return false;
		List<Event> myList = cHelper.getEventList();
		if (myList.isEmpty())
			return false;
		Iterator<?> it = myList.iterator();
		while (it.hasNext()) {
			Event node = (Event) it.next();
			mapping.put(node, null);
		}
		return true;
	}

	@Override
	public void execute() {
		try {
			Iterator<Event> it = mapping.keySet().iterator();
			while (it.hasNext()) {
				Event o = it.next();
				Event clone = o.clone();
				mapping.put(o, clone);
			}
			redo();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void redo() {
		Iterator<Event> it = mapping.values().iterator();
		while (it.hasNext()) {
			Event o = it.next();
			control.addEvent(o);
		}
	}

	@Override
	public boolean canUndo() {
		return !(mapping.isEmpty());
	}

	@Override
	public void undo() {
		Iterator<Event> it = mapping.values().iterator();
		while (it.hasNext()) {
			Event o = it.next();
			control.removeEvent(o);
		}
	}

	public HashMap<Event, Event> getList() {
		return this.mapping;
	}

	public void setControl(BControl control) {
		this.control = control;
	}

}
