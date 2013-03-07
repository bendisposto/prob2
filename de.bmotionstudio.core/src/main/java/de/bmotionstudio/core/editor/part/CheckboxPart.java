/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.core.editor.edit.TextEditManager;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.core.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.core.editor.figure.CheckboxFigure;
import de.bmotionstudio.core.model.control.BControl;

public class CheckboxPart extends BMSAbstractEditPart {

	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void handleStateChanged(ChangeEvent event) {
			if (event.getPropertyName().equals(ButtonModel.PRESSED_PROPERTY)) {
				AbstractBMotionFigure f = (AbstractBMotionFigure) getFigure();
				if (f.getModel().isPressed()) {
					// TODO: Reimplement me!!!
					// BControl control = (BControl) getModel();
					// // Recheck observer after click
					// control.getVisualization().getAnimation().checkObserver();
					// if (Boolean.valueOf(control.getAttributeValue(
					// AttributeConstants.ATTRIBUTE_CHECKED).toString())) {
					// control.setAttributeValue(
					// AttributeConstants.ATTRIBUTE_CHECKED, false);
					// } else {
					// control.setAttributeValue(
					// AttributeConstants.ATTRIBUTE_CHECKED, true);
					// }
				}
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
//		if (isRunning()) {
			if (getFigure() instanceof AbstractBMotionFigure)
				((AbstractBMotionFigure) getFigure())
						.addChangeListener(changeListener);
//		}
	}

	@Override
	public void deactivate() {
//		if (isRunning()) {
			if (getFigure() instanceof AbstractBMotionFigure)
				((AbstractBMotionFigure) getFigure())
						.removeChangeListener(changeListener);
//		}
		super.deactivate();
	}

	@Override
	protected IFigure createEditFigure() {
		CheckboxFigure fig = new CheckboxFigure();
		return fig;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent pEvent) {

		Object value = pEvent.getNewValue();
		String aID = pEvent.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((CheckboxFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_CHECKED)) {
			Boolean bol = Boolean.valueOf(value.toString());
			if (bol) {
				((CheckboxFigure) figure).setImage(BMotionImage
						.getImage(BMotionImage.IMG_ICON_CHECKED));
			} else {
				((CheckboxFigure) figure).setImage(BMotionImage
						.getImage(BMotionImage.IMG_ICON_UNCHECKED));
			}

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT)) {
			int addWidth = ((CheckboxFigure) figure).setText(value.toString());
			((BControl) getModel()).setAttributeValue(
					AttributeConstants.ATTRIBUTE_WIDTH, (30 + addWidth));
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR)) {
			RGB rgbText = (RGB) value;
			((CheckboxFigure) figure)
					.setTextColor(new org.eclipse.swt.graphics.Color(Display
							.getDefault(), rgbText));
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_ENABLED))
			((CheckboxFigure) figure).setBtEnabled(Boolean.valueOf(value
					.toString()));

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

}
