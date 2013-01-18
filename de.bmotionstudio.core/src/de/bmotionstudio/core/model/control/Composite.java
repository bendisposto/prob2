/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.model.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeImage;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Composite extends BControl {

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(new RGB(192, 192, 192)));
		initAttribute(new BAttributeImage(null));

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

}
