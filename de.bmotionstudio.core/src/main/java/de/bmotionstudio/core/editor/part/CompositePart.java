/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.google.inject.Injector;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSEditLayoutPolicy;
import de.bmotionstudio.core.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.core.editor.figure.CompositeFigure;
import de.bmotionstudio.core.editor.view.library.AbstractLibraryCommand;
import de.bmotionstudio.core.editor.view.library.AttributeRequest;
import de.bmotionstudio.core.editor.view.library.LibraryImageCommand;
import de.bmotionstudio.core.editor.view.library.LibraryVariableCommand;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class CompositePart extends BMSAbstractEditPart {

	private Injector injector = ServletContextListener.INJECTOR;
	
	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new CompositeFigure();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((CompositeFigure) figure).setBackgroundColor((RGB) value);

		// if (aID.equals(AttributeConstants.ATTRIBUTE_ALPHA))
		// ((BComposite) figure).setAlpha(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_IMAGE)) {

			if (value != null && value.toString().length() > 0) {
				// TODO: What is if we open the visualization without a running
				// model?
				final AnimationSelector selector = injector
						.getInstance(AnimationSelector.class);
				Trace currentHistory = selector.getCurrentTrace();
				if (currentHistory != null) {
					String path = currentHistory.getModel().getModelFile()
							.getParent()
							+ "/images";
					String imagePath = path + File.separator + value.toString();
					if (new File(imagePath).exists())
						((CompositeFigure) figure).setImage(new Image(Display
								.getDefault(), imagePath));
				}

			}

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE)) {
			Boolean visible = Boolean.valueOf(value.toString());
			((CompositeFigure) figure).setVisible(visible);
			// Handle children
			for (BControl child : getCastedModel().getChildren()) {
				child.setAttributeValue(AttributeConstants.ATTRIBUTE_VISIBLE,
						visible);
			}
		}

	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildren();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
			Boolean val = (Boolean) getViewer().getProperty(
					RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGuides(this));
			val = (Boolean) getViewer().getProperty(
					SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGeometry(this));
			val = (Boolean) getViewer().getProperty(
					SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGrid(this));

			if (snapStrategies.size() == 0)
				return null;
			if (snapStrategies.size() == 1)
				return snapStrategies.get(0);

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++)
				ss[i] = (SnapToHelper) snapStrategies.get(i);
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		AbstractLibraryCommand command = null;
		if (request.getAttributeTransferObject().getLibraryObject().getType()
				.equals("variable")) {
			command = new LibraryVariableCommand();
		} else if (request.getAttributeTransferObject().getLibraryObject()
				.getType().equals("image")) {
			command = new LibraryImageCommand();
		}
		return command;
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new BMSEditLayoutPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new SnapFeedbackPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

}
