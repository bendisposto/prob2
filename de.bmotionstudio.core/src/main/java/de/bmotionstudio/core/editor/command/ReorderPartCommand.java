/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.control.BControl;

public class ReorderPartCommand extends Command {

	private int oldIndex, newIndex;
	private BControl child;
	private BControl parent;

	public ReorderPartCommand(BControl child, BControl parent, int newIndex) {
		super("Reorder Control");
		this.child = child;
		this.parent = parent;
		this.newIndex = newIndex;
	}

	public void execute() {
		oldIndex = parent.getChildren().indexOf(child);
		parent.removeChild(child);
		parent.addChild(child, newIndex);
	}

	public void undo() {
		parent.removeChild(child);
		parent.addChild(child, oldIndex);
	}

}
