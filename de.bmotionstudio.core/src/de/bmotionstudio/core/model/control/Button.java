/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeEnabled;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeText;
import de.bmotionstudio.core.model.attribute.BAttributeTextColor;
import de.bmotionstudio.core.model.attribute.BAttributeWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Button extends BControl {

	public static transient String DEFAULT_TEXT = "Click!";

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeBackgroundColor(new RGB(192, 192, 192)));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeEnabled(true));

		BAttributeHeight aHeight = new BAttributeHeight(25);
		aHeight.setGroup(BAttributeSize.ID);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(100);
		aWidth.setGroup(BAttributeSize.ID);
		initAttribute(aWidth);

	}

}
