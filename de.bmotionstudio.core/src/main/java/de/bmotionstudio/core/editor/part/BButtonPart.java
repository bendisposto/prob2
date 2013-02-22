/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.core.editor.edit.TextEditManager;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.core.editor.figure.ButtonFigure;
import de.bmotionstudio.core.model.control.BControl;

public class BButtonPart extends BMSAbstractEditPart {

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT))
			((ButtonFigure) figure).setText(value.toString());

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((ButtonFigure) figure).setBackgroundColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR))
			((ButtonFigure) figure).setTextColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_ENABLED))
			((ButtonFigure) figure).setBtEnabled(Boolean.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((ButtonFigure) figure).setVisible(Boolean.valueOf(value.toString()));

	}

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new ButtonFigure();
		return figure;
	}

	private void performDirectEdit() {
		new TextEditManager(this, new TextCellEditorLocator(
				(IFigure) getFigure())).show();
	}

	@Override
	public void performRequest(Request request) {
		super.performRequest(request);
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new RenamePolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
