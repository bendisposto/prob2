/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.Observer;

public class RemoveObserverCommand extends Command {

	private BControl control;
	
	private Observer observer;
	
	public RemoveObserverCommand(Observer observer, BControl control) {
		this.observer = observer;
		this.control = control;
	}

	public void execute() {
		redo();
	}

	public boolean canExecute() {
		return observer != null && control != null;
	}

	public void undo() {
		control.addObserver(observer);
	}

	public void redo() {
		control.removeObserver(observer);
	}
	
	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return this.control;
	}

	public Observer getObserver() {
		return observer;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	

}
