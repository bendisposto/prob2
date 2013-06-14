/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.figure;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.core.BMotionImage;

/**
 * @author Lukas Ladenberger
 * 
 */
public class AbstractBMotionFigure extends Clickable {

	protected boolean visible;
	protected boolean isRunning;
	private boolean locked;
	public static final int HIDDEN_ALPHA_VALUE = 35;

	public AbstractBMotionFigure() {
		this.visible = true;
		this.isRunning = false;
		this.locked = false;
	}

	public void deactivateFigure() {
	}

	public void activateFigure() {
	}

	@Override
	public void setVisible(boolean visible) {
		// this.visible = visible;
		// repaint();
		//
		super.setVisible(visible);
	}

	@Override
	public void paint(Graphics g) {
		Rectangle clientArea = getClientArea();
		if (!this.visible) {
			g.drawImage(
					BMotionImage.getImage(BMotionImage.IMG_ICON_CONTROL_HIDDEN),
					clientArea.x, clientArea.y);
			g.setAlpha(HIDDEN_ALPHA_VALUE);
		}
		super.paint(g);
	}

	@Override
	public boolean isEnabled() {
		return isLocked();
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
}
