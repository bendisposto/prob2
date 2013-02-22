/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeBackgroundVisible;
import de.bmotionstudio.core.model.attribute.BAttributeFont;
import de.bmotionstudio.core.model.attribute.BAttributeText;
import de.bmotionstudio.core.model.attribute.BAttributeTextColor;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Text extends BControl {

	public static transient String DEFAULT_TEXT = "Text...";

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(ColorConstants.black.getRGB()));
		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.white.getRGB()));
		initAttribute(new BAttributeBackgroundVisible(true));
		initAttribute(new BAttributeFont(
				"1||9.75|0|WINDOWS|1|-13|0|0|0|400|0|0|0|0|0|0|0|0|"));

	}

}
