/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;

public class BMSEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		AbstractGraphicalEditPart part = null;

		BControl control = (BControl) model;

		if (control instanceof Visualization) {
			part = new VisualizationPart();
		} else {
			IBControlService service = BMotionEditorPlugin.getControlServicesClass()
					.get(control.getClass());
			if (service != null)
				part = service.createEditPart();
		}

		if (part != null)
			part.setModel(control);

		// TODO: check if part == null
		return part;

	}

}
