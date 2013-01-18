/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeForegroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeText;
import de.bmotionstudio.core.model.attribute.BAttributeTextColor;
import de.bmotionstudio.core.model.attribute.BAttributeVisible;

public class TableCell extends BControl {

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.white.getRGB()));

		BAttributeForegroundColor aForegroundColor = new BAttributeForegroundColor(
				ColorConstants.black.getRGB());
		aForegroundColor.setEditable(true);
		aForegroundColor.setShow(false);
		initAttribute(aForegroundColor);

		initAttribute(new BAttributeTextColor(ColorConstants.black.getRGB()));
		initAttribute(new BAttributeText(""));

		BAttributeHeight aHeight = new BAttributeHeight(20);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeVisible aVisible = new BAttributeVisible(true);
		aVisible.setShow(false);
		aVisible.setEditable(false);
		initAttribute(aVisible);

	}

}
