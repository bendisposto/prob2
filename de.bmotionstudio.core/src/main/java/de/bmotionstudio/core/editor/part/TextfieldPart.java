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

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.core.editor.edit.TextEditManager;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.core.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.core.editor.figure.TextfieldFigure;
import de.bmotionstudio.core.model.control.BControl;

public class TextfieldPart extends BMSAbstractEditPart {

	private TextEditManager textEditManager;

	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void handleStateChanged(ChangeEvent event) {
			if (event.getPropertyName().equals(ButtonModel.PRESSED_PROPERTY)) {
				AbstractBMotionFigure f = (AbstractBMotionFigure) getFigure();
				if (f.getModel().isPressed()) {
					if (textEditManager == null)
						textEditManager = new TextEditManager(
								TextfieldPart.this, new TextCellEditorLocator(
										(IFigure) getFigure())) {
							@Override
							protected void bringDown() {
								super.bringDown();
								// TODO: Reimplement me!!!
								// ((BControl) getModel()).getVisualization()
								// .getAnimation().checkObserver();
							}

						};
					textEditManager.show();
				}
			}
		}
	};

	@Override
	protected IFigure createEditFigure() {
		TextfieldFigure figure = new TextfieldFigure();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT))
			((TextfieldFigure) figure).setText(value.toString());

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((TextfieldFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

	}

	private void performDirectEdit() {
		new TextEditManager(TextfieldPart.this, new TextCellEditorLocator(
				(IFigure) getFigure())).show();
	}

	@Override
	public void performRequest(Request request) {
		super.performRequest(request);
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

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
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new RenamePolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

}
