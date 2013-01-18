/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.part;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;

public class BMSTreeEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		BMSAbstractTreeEditPart part = null;

		if (model instanceof Visualization) {
			part = new BControlTreeEditPart();
		} else if (model instanceof BControl) {

			BControl control = (BControl) model;

			IBControlService service = BMotionEditorPlugin.getControlServices()
					.get(control.getClass());
			part = service.createTreeEditPart();

		}

		if (part != null)
			part.setModel(model);

		return part;

	}

}
