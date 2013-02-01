/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.core.model.attribute.AttributeFillColor;
import de.bmotionstudio.core.model.attribute.AttributeFillHeight;
import de.bmotionstudio.core.model.attribute.AttributeMeasureInterval;
import de.bmotionstudio.core.model.attribute.AttributeMeasureMaxPos;
import de.bmotionstudio.core.model.attribute.AttributeShowMeasure;
import de.bmotionstudio.core.model.attribute.BAttributeAlpha;
import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;

public class Tank extends BControl {

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.black.getRGB()));
		initAttribute(new AttributeShowMeasure(true));
		initAttribute(new AttributeMeasureInterval(25));
		initAttribute(new AttributeMeasureMaxPos(100));
		initAttribute(new AttributeFillColor(ColorConstants.blue.getRGB()));
		initAttribute(new AttributeFillHeight(75));
		initAttribute(new BAttributeAlpha(0));

	}

}
