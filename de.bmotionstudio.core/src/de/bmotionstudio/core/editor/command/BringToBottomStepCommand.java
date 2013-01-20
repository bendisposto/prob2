/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.command;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BControlPropertyConstants;

public class BringToBottomStepCommand extends AbstractBringToCommand {

	public void execute() {
		for (BControl control : getControlList()) {
			BControl parent = control.getParent();
			Integer oldIndex = parent.getChildren().indexOf(control);
			getOldIndexMap().put(control, oldIndex);
			if (oldIndex > 0) {
				parent.getChildren().remove(control);
				parent.getChildren().add(oldIndex - 1, control);
				parent.getPropertyChangeSupport().firePropertyChange(
						BControlPropertyConstants.PROPERTY_ADD_CHILD, null,
						null);
			}
		}
	}

	public void undo() {
		for (BControl control : getControlList()) {
			BControl parent = control.getParent();
			parent.getChildren().remove(control);
			parent.getChildren().add(getOldIndexMap().get(control),
					control);
			parent.getPropertyChangeSupport().firePropertyChange(
					BControlPropertyConstants.PROPERTY_ADD_CHILD, null, null);
		}
	}

}
