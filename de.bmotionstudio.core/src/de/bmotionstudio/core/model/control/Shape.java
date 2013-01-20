/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.model.attribute.BAttributeAlpha;
import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeDirection;
import de.bmotionstudio.core.model.attribute.BAttributeFillType;
import de.bmotionstudio.core.model.attribute.BAttributeForegroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeImage;
import de.bmotionstudio.core.model.attribute.BAttributeOrientation;
import de.bmotionstudio.core.model.attribute.BAttributeOutlineAlpha;
import de.bmotionstudio.core.model.attribute.BAttributeShape;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Shape extends BControl {

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(new RGB(255, 0, 0)));
		initAttribute(new BAttributeForegroundColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeImage(null));
		initAttribute(new BAttributeAlpha(255));
		initAttribute(new BAttributeOutlineAlpha(0));
		initAttribute(new BAttributeShape(BAttributeShape.SHAPE_RECTANGLE));
		initAttribute(new BAttributeOrientation(
				BAttributeOrientation.HORIZONTAL));
		initAttribute(new BAttributeDirection(BAttributeDirection.NORTH));
		initAttribute(new BAttributeFillType(BAttributeFillType.FILLED));

	}

}
