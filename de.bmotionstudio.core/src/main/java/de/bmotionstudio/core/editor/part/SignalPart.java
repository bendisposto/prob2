/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.figure.SignalFigure;
import de.bmotionstudio.core.model.control.BControl;

public class SignalPart extends BMSAbstractEditPart {

	private ChopboxAnchor fixedConnectionAnchor;
	
	@Override
	protected IFigure createEditFigure() {
		return new SignalFigure();
	}

	private void refreshAnchors() {
		fixedConnectionAnchor = null;
		List<?> connections = getSourceConnections();
		for(Object o : connections)
			((BConnectionEditPart) o).refresh();
		connections = getTargetConnections();
		for(Object o : connections)
			((BConnectionEditPart) o).refresh();		
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
			refreshAnchors();
		}
		
		if (aID.equals(AttributeConstants.ATTRIBUTE_LABEL))
			((SignalFigure) getFigure()).setLabel(value.toString());

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}
	
	protected ConnectionAnchor getConnectionAnchor() {
		if (fixedConnectionAnchor == null) {
			fixedConnectionAnchor = new ChopboxAnchor(getFigure()) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle r = getFigure().getBounds();
					int y = r.y + r.height / 2 + 7;
					int x = r.x + r.width / 2;
					if (((SignalFigure) getFigure()).isRight()) {
						x = x + 18;
					} else {
						x = x - 18;
					}
					Point p = new PrecisionPoint(x, y);
					getFigure().translateToAbsolute(p);
					return p;
				}
			};
		}
		return fixedConnectionAnchor;
	}

}
