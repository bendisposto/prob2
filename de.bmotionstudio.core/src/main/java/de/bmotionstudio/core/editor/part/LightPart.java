/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.figure.LightFigure;
import de.bmotionstudio.core.model.control.BControl;

public class LightPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new LightFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((LightFigure) figure).setBackgroundColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((LightFigure) figure)
					.setVisible(Boolean.valueOf(value.toString()));

	}

	@Override
	protected void prepareEditPolicies() {
	}

}
