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

import de.bmotionstudio.core.model.observer.Observer;

public class CopyObserverCommand extends Command {

	private List<Observer> observerList = new ArrayList<Observer>();

	public CopyObserverCommand(List<Observer> observerList) {
		this.observerList = observerList;
	}
	
	@Override
	public boolean canExecute() {
		if (observerList == null || observerList.isEmpty())
			return false;
		return true;
	}

	@Override
	public void execute() {
		if (canExecute())
			Clipboard.getDefault().setContents(
					new CopyPasteHelper(observerList));
	}

	@Override
	public boolean canUndo() {
		return false;
	}

}
