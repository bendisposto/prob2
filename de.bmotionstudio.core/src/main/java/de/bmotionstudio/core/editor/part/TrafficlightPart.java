/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.command.CreateCommand;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.SignalLayoutEditPolicy;
import de.bmotionstudio.core.editor.figure.TrafficlightFigure;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Light;

public class TrafficlightPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new TrafficlightFigure();
	}

	@Override
	protected void refreshEditLayout(IFigure figure, BControl control) {

		int lights = Integer.valueOf(control.getAttributeValue(
				AttributeConstants.ATTRIBUTE_LIGHTS).toString());

		figure.getParent().setConstraint(
				figure,
				new Rectangle(control.getLocation().x, control.getLocation().y,
						control.getDimension().width, lights * 12 + 30));

	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_LIGHTS)) {

			if (oldValue == null || value.equals(oldValue))
				return;

			// Create lights
			Integer numberOfLights = Integer.valueOf(value.toString());
			Integer numberOfCurrentLights = Integer
					.valueOf(oldValue.toString());

			if (numberOfLights < numberOfCurrentLights) {
				for (int i = numberOfCurrentLights - 1; i >= numberOfLights; i--) {
					model.removeChild(i);
				}
			}

			for (int i = numberOfCurrentLights; i < numberOfLights; i++) {
				Light light = new Light();
				CreateCommand cmd = new CreateCommand(light, model);
				cmd.execute();
			}

			refreshEditLayout(figure, model);

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_LABEL)) {
			((TrafficlightFigure) getFigure()).setLabel(value.toString());
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TRACK_DIRECTION)) {
			int direction = Integer.valueOf(value.toString());
			if (direction == 1) {
				((TrafficlightFigure) getFigure()).setTrackDirection(false);
			} else {
				((TrafficlightFigure) getFigure()).setTrackDirection(true);
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((TrafficlightFigure) figure)
					.setVisible(Boolean.valueOf(value.toString()));

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new SignalLayoutEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildren();
	}

}
