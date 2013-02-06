package de.bmotionstudio.core.service;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.BShapePart;
import de.bmotionstudio.core.model.attribute.BAttributeShape;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Shape;

public class BEllipseService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		Shape ellipse = new Shape();
		ellipse.setAttributeValue(AttributeConstants.ATTRIBUTE_SHAPE,
				BAttributeShape.SHAPE_OVAL);
		return ellipse;
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
