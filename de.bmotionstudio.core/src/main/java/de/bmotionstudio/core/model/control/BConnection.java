/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.control;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.attribute.BAttributeConnection;
import de.bmotionstudio.core.model.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.core.model.attribute.BAttributeConnectionTargetDecoration;
import de.bmotionstudio.core.model.attribute.BAttributeForegroundColor;
import de.bmotionstudio.core.model.attribute.BAttributeLabel;
import de.bmotionstudio.core.model.attribute.BAttributeLineStyle;
import de.bmotionstudio.core.model.attribute.BAttributeLineWidth;

public class BConnection extends BControl {

	protected boolean isConnected;

	protected String source;

	protected String target;

	/**
	 * Disconnect this connection from the shapes it is attached to.
	 */
	public void disconnect() {
		if (isConnected) {
			BControl sourceControl = getVisualization().getBControl(source);
			BControl targetControl = getVisualization().getBControl(target);
			if(sourceControl != null)
				sourceControl.removeConnection(this);
			if(targetControl != null)
				targetControl.removeConnection(this);
			isConnected = false;
		}
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String c) {
		this.target = c;
	}

	public void setSource(String c) {
		this.source = c;
	}

	/**
	 * Reconnect this connection. The connection will reconnect with the shapes
	 * it was previously attached to.
	 */
	public void reconnect() {
		if (!isConnected) {
			BControl sourceControl = getVisualization().getBControl(source);
			BControl targetControl = getVisualization().getBControl(target);
			sourceControl.addConnection(this);
			targetControl.addConnection(this);
			isConnected = true;
		}
	}

	/**
	 * Reconnect to a different source and/or target shape. The connection will
	 * disconnect from its current attachments and reconnect to the new source
	 * and target.
	 * 
	 * @param newSource
	 *            a new source endpoint for this connection (non null)
	 * @param newTarget
	 *            a new target endpoint for this connection (non null)
	 * @throws IllegalArgumentException
	 *             if any of the paramers are null or newSource == newTarget
	 */
	public void reconnect(String newSource, String newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}

	@Override
	protected void initAttributes() {

		BAttributeConnection aConnection = new BAttributeConnection(null);
		aConnection.setGroup(AbstractAttribute.ROOT);
		initAttribute(aConnection);

		BAttributeLineWidth aLineWidth = new BAttributeLineWidth(1);
		aLineWidth.setGroup(aConnection);
		initAttribute(aLineWidth);

		BAttributeLineStyle aLineStyle = new BAttributeLineStyle(
				BAttributeLineStyle.SOLID_CONNECTION);
		aLineStyle.setGroup(aConnection);
		initAttribute(aLineStyle);

		BAttributeForegroundColor aForegroundColor = new BAttributeForegroundColor(
				new RGB(0, 0, 0));
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

	}

}