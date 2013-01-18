/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.model.attribute.BAttributeChecked;
import de.bmotionstudio.core.model.attribute.BAttributeEnabled;
import de.bmotionstudio.core.model.attribute.BAttributeFalseValue;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeText;
import de.bmotionstudio.core.model.attribute.BAttributeTextColor;
import de.bmotionstudio.core.model.attribute.BAttributeTrueValue;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Checkbox extends BControl {

	public static transient String DEFAULT_TEXT = "Text...";

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeEnabled(true));
		initAttribute(new BAttributeChecked(true));
		initAttribute(new BAttributeTrueValue(""));
		initAttribute(new BAttributeFalseValue(""));

		BAttributeHeight aHeight = new BAttributeHeight(21);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

	}

	@Override
	public String getValueOfData() {
		if (Boolean.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_CHECKED).toString())) {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_TRUEVALUE)
					.toString();
		} else {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_FALSEVALUE)
					.toString();
		}
	}

}
