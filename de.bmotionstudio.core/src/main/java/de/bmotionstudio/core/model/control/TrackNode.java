/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class TrackNode extends BControl {

	@Override
	protected void initAttributes() {

		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		aSize.setShow(false);
		aSize.setEditable(false);
		initAttribute(aSize);

		BAttributeHeight aHeight = new BAttributeHeight(12);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(8);
		aWidth.setGroup(BAttributeSize.ID);
		aWidth.setShow(false);
		aWidth.setEditable(false);
		initAttribute(aWidth);

	}

}
