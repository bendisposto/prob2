/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.AttributeTrackLength;
import de.bmotionstudio.core.model.attribute.BAttributeConnection;
import de.bmotionstudio.core.model.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.core.model.attribute.BAttributeConnectionTargetDecoration;
import de.bmotionstudio.core.model.attribute.BAttributeForegroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeLabel;
import de.bmotionstudio.core.model.attribute.BAttributeLineStyle;
import de.bmotionstudio.core.model.attribute.BAttributeLineWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Segment extends BConnection {

	@Override
	protected void initAttributes() {

		BAttributeConnection aConnection = new BAttributeConnection(null);
		aConnection.setGroup(AbstractAttribute.ROOT);
		initAttribute(aConnection);

		BAttributeLineWidth aLineWidth = new BAttributeLineWidth(4);
		aLineWidth.setGroup(aConnection);
		initAttribute(aLineWidth);

		BAttributeLineStyle aLineStyle = new BAttributeLineStyle(
				BAttributeLineStyle.SOLID_CONNECTION);
		aLineStyle.setGroup(aConnection);
		initAttribute(aLineStyle);

		BAttributeForegroundColor aForegroundColor = new BAttributeForegroundColor(
				ColorConstants.gray.getRGB());
		aForegroundColor.setGroup(aConnection);
		initAttribute(aForegroundColor);

		BAttributeConnectionSourceDecoration aSourceDeco = new BAttributeConnectionSourceDecoration(
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		aSourceDeco.setGroup(aConnection);
		initAttribute(aSourceDeco);

		BAttributeConnectionTargetDecoration aTargetDeco = new BAttributeConnectionTargetDecoration(
				BAttributeConnectionSourceDecoration.DECORATION_NONE);
		aTargetDeco.setGroup(aConnection);
		initAttribute(aTargetDeco);

		BAttributeLabel aLabel = new BAttributeLabel("Label ...");
		aLabel.setGroup(aConnection);
		initAttribute(aLabel);
		
		AttributeTrackLength attributeTrackLength = new AttributeTrackLength(
				250);
		attributeTrackLength.setGroup(aConnection);
		initAttribute(attributeTrackLength);
		
	}

}
