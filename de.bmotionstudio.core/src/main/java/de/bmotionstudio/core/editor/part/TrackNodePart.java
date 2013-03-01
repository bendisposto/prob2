/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;

import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.TrackEditPolicy;
import de.bmotionstudio.core.editor.figure.TrackNodeFigure;
import de.bmotionstudio.core.model.control.BControl;

public class TrackNodePart extends BMSAbstractEditPart {

	protected Color foregroundColor;

	@Override
	protected IFigure createEditFigure() {
		return new TrackNodeFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

//		Object value = evt.getNewValue();
//		String aID = evt.getPropertyName();

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TrackEditPolicy());
	}

}
