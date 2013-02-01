/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.editor.command.ConnectionCreateCommand;
import de.bmotionstudio.core.editor.command.CreateCommand;
import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.AttributeSwitchDirection;
import de.bmotionstudio.core.model.attribute.AttributeSwitchPosition;
import de.bmotionstudio.core.model.attribute.BAttributeHeight;
import de.bmotionstudio.core.model.attribute.BAttributeSize;
import de.bmotionstudio.core.model.attribute.BAttributeWidth;

public class Switch extends BControl {

	private transient Track track1;

	private transient Track track2;

	public Switch() {

		super();

		// Build up switch
		TrackNode tracknode1 = new TrackNode();
		CreateCommand cmd = new CreateCommand(tracknode1, this);
		cmd.setLayout(new Rectangle(5, 0, 50, 20));
		cmd.execute();

		TrackNode tracknode2 = new TrackNode();
		cmd = new CreateCommand(tracknode2, this);
		cmd.setLayout(new Rectangle(70, 0, 50, 20));
		cmd.execute();

		TrackNode tracknode3 = new TrackNode();
		cmd = new CreateCommand(tracknode3, this);
		cmd.setLayout(new Rectangle(70, 70, 50, 20));
		cmd.execute();

		ConnectionCreateCommand trackCreateCmd = new ConnectionCreateCommand(
				tracknode1);
		trackCreateCmd.setTarget(tracknode2);
		track1 = new Track();
		trackCreateCmd.setConnection(track1);
		trackCreateCmd.execute();

		trackCreateCmd = new ConnectionCreateCommand(tracknode1);
		trackCreateCmd.setTarget(tracknode3);
		track2 = new Track();
		trackCreateCmd.setConnection(track2);
		trackCreateCmd.execute();

		track1.setAttributeValue(AttributeConstants.ATTRIBUTE_LABEL, "");
		track2.setAttributeValue(AttributeConstants.ATTRIBUTE_LABEL, "");

		track1.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "LEFT");
		AbstractAttribute a1 = track1
				.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM);
		a1.setEditable(false);
		a1.setShow(false);

		track2.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "RIGHT");
		AbstractAttribute a2 = track2.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM);
		a2.setEditable(false);
		a2.setShow(false);

		tracknode1.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "1");
		tracknode1.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode1.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setShow(
				false);
		tracknode1.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

		tracknode2.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "2");
		tracknode2.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode2.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setShow(
				false);
		tracknode2.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

		tracknode3.setAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM, "3");
		tracknode3.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM)
				.setEditable(false);
		tracknode3.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM).setShow(
				false);
		tracknode3.getAttribute(AttributeConstants.ATTRIBUTE_COORDINATES)
				.setShow(false);

	}

	@Override
	protected void initAttributes() {

		BAttributeHeight aHeight = new BAttributeHeight(50);
		aHeight.setGroup(BAttributeSize.ID);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(100);
		aWidth.setGroup(BAttributeSize.ID);
		initAttribute(aWidth);

		initAttribute(new AttributeSwitchDirection(
				AttributeSwitchDirection.RIGHT_SOUTH));
		initAttribute(new AttributeSwitchPosition(
				AttributeSwitchPosition.UNKNOWN));

	}

	// We have to set the two tracks of the switch, since their are set to
	// transient
	protected Object readResolve() {

		super.readResolve();

		for (BControl control : getChildren()) {

			List<Track> tracks = new ArrayList<Track>();
			for (BConnection c : ((TrackNode) control).getSourceConnections()) {
				if (c instanceof Track)
					tracks.add((Track) c);
			}
			for (BConnection c : ((TrackNode) control).getTargetConnections()) {
				if (c instanceof Track)
					tracks.add((Track) c);
			}
			for (Track n : tracks) {
				AbstractAttribute a2 = n
						.getAttribute(AttributeConstants.ATTRIBUTE_CUSTOM);
				if (a2.getValue().equals("LEFT")) {
					a2.setEditable(false);
					a2.setShow(false);
					track1 = n;
				} else if (a2.getValue().equals("RIGHT")) {
					track2 = n;
					a2.setEditable(false);
					a2.setShow(false);
				}
			}

		}

		return this;

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

	public Track getTrack1() {
		return track1;
	}

	public Track getTrack2() {
		return track2;
	}

}
