/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;


public class TrackNodeFigure extends AbstractBMotionFigure {

	private Shape shapeFigure;

	private boolean showFeedback;
	
	public TrackNodeFigure() {

		setLayoutManager(new StackLayout());

		shapeFigure = new Shape() {

			@Override
			protected void outlineShape(Graphics g) {
			}

			@Override
			protected void fillShape(Graphics g) {
				Rectangle r = getBounds();
				g.drawRectangle(r.x, r.y, r.width - 1, r.height - 1);
				if(showFeedback)
					g.fillRectangle(r.x, r.y, r.width - 1, r.height - 1);
			}

		};

		add(shapeFigure);

	}

	@Override
	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
	}

	@Override
	public void deactivateFigure() {
	}

	public boolean isShowFeedback() {
		return showFeedback;
	}

	public void setShowFeedback(boolean showFeedback) {
		this.showFeedback = showFeedback;
		shapeFigure.repaint();
	}
	
}
