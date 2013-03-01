/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.figure.ButtonFigure;
import de.bmotionstudio.core.editor.figure.ShapeFigure;
import de.bmotionstudio.core.editor.figure.SignalFigure;
import de.bmotionstudio.core.editor.figure.TrafficlightFigure;
import de.bmotionstudio.core.model.control.BControl;

public class SignalPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new SignalFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
//		Object oldValue = evt.getOldValue();
		String aID = evt.getPropertyName();
		
		if (aID.equals(AttributeConstants.ATTRIBUTE_TRACK_DIRECTION)) {
			int direction = Integer.valueOf(value.toString());
			if (direction == 1) {
				((SignalFigure) getFigure()).setTrackDirection(false);
			} else {
				((SignalFigure) getFigure()).setTrackDirection(true);
			}
		}

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
	}

}
