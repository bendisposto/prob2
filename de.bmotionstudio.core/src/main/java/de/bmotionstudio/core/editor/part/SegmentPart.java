/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMSFontConstants;
import de.bmotionstudio.core.editor.command.ConnectionDeleteCommand;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Segment;

public class SegmentPart extends BConnectionEditPart {

	private Label conLabel;
	
	@Override
	protected void prepareEditPolicies() {
		// Selection handle edit policy.
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy()); // Allows the removal of
														// the connection model
														// element
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new ConnectionEditPolicy() {
					protected Command getDeleteCommand(GroupRequest request) {
						return new ConnectionDeleteCommand((Segment) getModel());
					}
				});
	}
	
	@Override
	protected IFigure createEditFigure() {
		IFigure figure = super.createEditFigure();
		conLabel = new Label();
		conLabel.setFont(BMSFontConstants.FONT_SMALL);
		ConnectionEndpointLocator locator = new ConnectionEndpointLocator(
				(Connection) figure, false);
		locator.setVDistance(1);
		figure.add(conLabel, locator);
		return figure;
	}
	
	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {
		
		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_TRACK_LENGTH))
			conLabel.setText(value.toString());
		
		super.refreshEditFigure(figure, model, evt);
		
	}	

}
