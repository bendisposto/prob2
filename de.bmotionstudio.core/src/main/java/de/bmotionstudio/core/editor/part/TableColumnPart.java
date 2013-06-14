/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.TableLayoutEditPolicy;
import de.bmotionstudio.core.editor.figure.TableColumnFigure;
import de.bmotionstudio.core.model.control.BControl;

public class TableColumnPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		TableColumnFigure tableColumnFigure = new TableColumnFigure();
		Label figure = new Label();
		tableColumnFigure.add(figure);
//		if (!isRunning()) {
			figure.setIcon(BMotionImage
					.getImage(BMotionImage.IMG_ICON_TR_UP));
//		}
		return tableColumnFigure;
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new TableLayoutEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR)) {
			((TableColumnFigure) figure).setForegroundColor((RGB) value);
			for (BControl cell : model.getChildren())
				cell.setAttributeValue(
						AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR, value);
		}

	}

	@Override
	protected void refreshEditLayout(IFigure figure, BControl control) {

		int width = control.getDimension().width;

		// Change width of all cells
		List<BControl> cells = control.getChildren();
		for (BControl cell : cells) {
			cell.setAttributeValue(AttributeConstants.ATTRIBUTE_WIDTH, width,
					true, true);
		}

		// Notify parent table about change
		if (getParent() instanceof BMSAbstractEditPart) {
			BMSAbstractEditPart tablePart = (BMSAbstractEditPart) getParent();
			tablePart.refreshEditLayout(tablePart.getFigure(),
					control.getParent());
		}

		super.refreshEditLayout(figure, control);

	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildren();
	}

}
