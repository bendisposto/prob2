/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import java.beans.PropertyChangeEvent;
import java.io.File;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import com.google.inject.Injector;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.core.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.core.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.core.editor.figure.BMSImageFigure;
import de.bmotionstudio.core.editor.view.library.AbstractLibraryCommand;
import de.bmotionstudio.core.editor.view.library.AttributeRequest;
import de.bmotionstudio.core.editor.view.library.LibraryImageCommand;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.webconsole.ServletContextListener;

public class BImagePart extends BMSAbstractEditPart {
	
	private Injector injector = ServletContextListener.INJECTOR;
	
	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

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
						((BMSImageFigure) figure).setImage(imagePath);
				}

			}
					
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((BMSImageFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_ALPHA))
			((BMSImageFigure) figure)
					.setAlpha(Integer.valueOf(value.toString()));

	}

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new BMSImageFigure();
		return figure;
	}

	@Override
	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		AbstractLibraryCommand command = null;
		if (request.getAttributeTransferObject().getLibraryObject().getType()
				.equals("image")) {
			command = new LibraryImageCommand();
		}
		return command;
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

}
