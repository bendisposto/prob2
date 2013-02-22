/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import java.util.Collection;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.ButtonGroupHelper;
import de.bmotionstudio.core.model.attribute.BAttributeButtonGroup;
import de.bmotionstudio.core.model.attribute.BAttributeChecked;
import de.bmotionstudio.core.model.attribute.BAttributeEnabled;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeText;
import de.bmotionstudio.core.model.attribute.BAttributeTextColor;
import de.bmotionstudio.core.model.attribute.BAttributeValue;

/**
 * @author Lukas Ladenberger
 * 
 */
public class RadioButton extends BControl {

	public static transient String DEFAULT_TEXT = "Text...";

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeEnabled(true));
		initAttribute(new BAttributeChecked(true));
		initAttribute(new BAttributeValue(""));
		initAttribute(new BAttributeButtonGroup(""));

		BAttributeHeight aHeight = new BAttributeHeight(21);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

	}

	@Override
	public String getValueOfData() {
		String btgroupid = getAttributeValue(
				AttributeConstants.ATTRIBUTE_BUTTONGROUP).toString();
		if (!btgroupid.trim().equals("")) {
			Collection<BControl> btGroup = ButtonGroupHelper
					.getButtonGroup(btgroupid);
			return getValueFromButtonGroup(btGroup);
		} else {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_VALUE)
					.toString();
		}
	}

	private String getValueFromButtonGroup(Collection<BControl> group) {
		for (BControl control : group) {
			if (Boolean.valueOf(control.getAttributeValue(
					AttributeConstants.ATTRIBUTE_CHECKED).toString())) {
				return control.getAttributeValue(
						AttributeConstants.ATTRIBUTE_VALUE).toString();
			}
		}
		return "";
	}

}
