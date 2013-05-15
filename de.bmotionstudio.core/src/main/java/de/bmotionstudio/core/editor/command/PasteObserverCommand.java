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
import de.bmotionstudio.core.model.observer.Observer;

public class PasteObserverCommand extends Command {

	private CopyPasteHelper cHelper;

	private HashMap<Observer, Observer> mappingObserver = new HashMap<Observer, Observer>();

	private BControl control;

	@Override
	public boolean canExecute() {
		cHelper = (CopyPasteHelper) Clipboard.getDefault().getContents();
		if (cHelper == null || control == null)
			return false;
		List<Observer> myList = cHelper.getObserverList();
		if (myList.isEmpty())
			return false;
		Iterator<?> it = myList.iterator();
		while (it.hasNext()) {
			Observer node = (Observer) it.next();
			mappingObserver.put(node, null);
		}
		return true;
	}

	@Override
	public void execute() {
		try {
			Iterator<Observer> it = mappingObserver.keySet().iterator();
			while (it.hasNext()) {
				Observer o = it.next();
				Observer clone = o.clone();
				mappingObserver.put(o, clone);
			}
			redo();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void redo() {
		Iterator<Observer> it = mappingObserver.values().iterator();
		while (it.hasNext()) {
			Observer o = it.next();
			control.addObserver(o);
		}
	}

	@Override
	public boolean canUndo() {
		return !(mappingObserver.isEmpty());
	}

	@Override
	public void undo() {
		Iterator<Observer> it = mappingObserver.values().iterator();
		while (it.hasNext()) {
			Observer o = it.next();
			control.removeObserver(o);
		}
	}

	public HashMap<Observer, Observer> getList() {
		return this.mappingObserver;
	}
	
	public void setControl(BControl control) {
		this.control = control;
	}

}
