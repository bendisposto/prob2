/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.BControlPropertyConstants;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.model.observer.IObserverListener;
import de.bmotionstudio.core.model.observer.Observer;

public class BControlTreeEditPart extends BMSAbstractTreeEditPart implements
		PropertyChangeListener, IObserverListener {

	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(
				BControlPropertyConstants.PROPERTY_ADD_CHILD)
				|| evt.getPropertyName().equals(
						BControlPropertyConstants.PROPERTY_REMOVE_CHILD)) {
			refreshChildren();
		}
		refreshVisuals();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
	}

	@Override
	protected List<Object> getModelChildren() {

		List<Object> toShowElements = new ArrayList<Object>();

		if (getModel() instanceof BControl) {

			BControl c = (BControl) getModel();

			for (BControl control : c.getChildren()) {
				if (BMotionEditorPlugin.getControlServicesClass()
						.get(control.getClass()).showInOutlineView())
					toShowElements.add(control);
				List<BConnection> sourceConnections = control
						.getSourceConnections();
				for (BConnection con : sourceConnections) {
					if (BMotionEditorPlugin.getControlServicesClass()
							.get(con.getClass()).showInOutlineView()
							&& !toShowElements.contains(con))
						toShowElements.add(con);
				}
				List<BConnection> targetConnections = control
						.getTargetConnections();
				for (BConnection con : targetConnections) {
					if (BMotionEditorPlugin.getControlServicesClass()
							.get(con.getClass()).showInOutlineView()
							&& !toShowElements.contains(con))
						toShowElements.add(con);
				}
			}

		}

		return toShowElements;

	}

	public void activate() {
		if (!isActive()) {
			super.activate();
			((BControl) getModel()).addPropertyChangeListener(this);
			// ((BControl) getModel()).addObserverListener(this);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((BControl) getModel()).removePropertyChangeListener(this);
			// ((BControl) getModel()).removeObserverListener(this);
		}
	}

	@Override
	public void refreshVisuals() {

		Object model = getModel();

		if (model instanceof BControl) {
			BControl bcontrol = (BControl) model;
			if (!(bcontrol instanceof Visualization)) {
				setWidgetText(bcontrol.getAttributeValue(
						AttributeConstants.ATTRIBUTE_ID).toString());
				setWidgetImage(bcontrol.getIcon());
			}
		} else if (model instanceof Observer) {
			setWidgetText(((Observer) model).getName());
			setWidgetImage(BMotionImage
					.getImage(BMotionImage.IMG_ICON_OBSERVER));
		}

	}

	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			try {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				page.showView(IPageLayout.ID_PROP_SHEET);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addedObserver(BControl control, Observer observer) {
		refreshChildren();
	}

	@Override
	public void removedObserver(BControl control) {
		refreshChildren();
	}

}
