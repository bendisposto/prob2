/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import de.bmotionstudio.core.model.event.Event;

public class CopyEventCommand extends Command {

	private List<Event> list = new ArrayList<Event>();

	public CopyEventCommand(List<Event> list) {
		this.list = list;
	}

	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		return true;
	}

	@Override
	public void execute() {
		if (canExecute()) {
			CopyPasteHelper copyPasteHelper = new CopyPasteHelper();
			copyPasteHelper.setEventList(list);
			Clipboard.getDefault().setContents(copyPasteHelper);
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

}
