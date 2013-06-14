/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeText;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Textfield extends BControl {

	public static transient String DEFAULT_TEXT = "Text...";

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));

		BAttributeHeight aHeight = new BAttributeHeight(21);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

	}

	@Override
	public String getValueOfData() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_TEXT).toString();
	}

}
