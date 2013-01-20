/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.figure.TankFigure;
import de.bmotionstudio.core.model.control.BControl;

public class TankPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new TankFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((TankFigure) figure).setVisible(Boolean.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_ALPHA))
			((TankFigure) figure).setAlpha(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_FILL_COLOR))
			((TankFigure) figure).setFillColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_FILL_HEIGHT))
			((TankFigure) figure).setFillHeight(Integer.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_MEASURE_MAXPOS))
			((TankFigure) figure).setMaxPos(Integer.valueOf(model
					.getAttributeValue(
							AttributeConstants.ATTRIBUTE_MEASURE_MAXPOS)
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_MEASURE_INTERVAL))
			((TankFigure) figure)
					.setInterval(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_SHOWS_MEASURE))
			((TankFigure) figure).setMeasure(Boolean.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((TankFigure) figure).setBackgroundColor((RGB) value);

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
