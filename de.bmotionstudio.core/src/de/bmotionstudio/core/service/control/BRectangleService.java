/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.service.control;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.BShapePart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Shape;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BRectangleService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new Shape();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BShapePart();
	}

	@Override
	public Class<?> getControlClass() {
		return Shape.class;
	}

}
