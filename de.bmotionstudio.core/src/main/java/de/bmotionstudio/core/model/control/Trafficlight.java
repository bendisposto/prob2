/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.model.control;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.core.editor.command.CreateCommand;
import de.bmotionstudio.core.model.attribute.AttributeLights;
import de.bmotionstudio.core.model.attribute.AttributeTrackDirection;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeLabel;
import de.bmotionstudio.core.model.attribute.BAttributeSize;

/**
 * @author Lukas Ladenberger
 * 
 */
public class Trafficlight extends BControl {

	public Trafficlight() {

		super();

		int numberOfLights = 2;

		CreateCommand cmd;
		for (int i = 0; i < numberOfLights; i++) {
			Light light = new Light();
			cmd = new CreateCommand(light, this);
			cmd.setLayout(new Rectangle(0, 0, 12, 12));
			cmd.execute();
		}

	}

	@Override
	protected void initAttributes() {

		BAttributeHeight aHeight = new BAttributeHeight(48);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		initAttribute(new AttributeTrackDirection(AttributeTrackDirection.RIGHT));
		initAttribute(new AttributeLights(2));

		BAttributeLabel aLabel = new BAttributeLabel("Signal");
		initAttribute(aLabel);

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

}
