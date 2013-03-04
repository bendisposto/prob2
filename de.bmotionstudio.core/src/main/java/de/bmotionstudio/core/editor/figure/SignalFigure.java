/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Lukas Ladenberger
 * 
 */
public class SignalFigure extends AbstractBMotionFigure {

	private Shape shapeFigure;
	protected static final int LEG = 8;
	protected static final int RAD = 10;
	public static final int WIDTH = LEG + RAD*2+1;
	public static final int HEIGHT = RAD*2+2;
	private boolean isRight;
	private Label label;
	private Color foregroundcolor;
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	public SignalFigure() {
		
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		setLayoutManager(layout);

		setOpaque(true);
		
		label = new Label();
		
		shapeFigure = new Shape() {

			@Override
			protected void outlineShape(Graphics g) {

				Rectangle r = this.getBounds();

				g.setAntialias(SWT.ON);

				Point a;
				Point b;
				Point c;

				int x = r.x + r.width / 2;
				int y = r.y + r.height / 2;
				
				if (isRight) {

					x = x - 10;
					g.drawLine(x, y, x + LEG + RAD * 2, y);
					g.drawOval(x, y - RAD, RAD * 2, RAD * 2);
					g.drawLine(x + RAD - 1, y - RAD, x + RAD - 1, y + RAD);
					g.drawLine(x + RAD + 1, y - RAD, x + RAD + 1, y + RAD);

					a = new Point(x + RAD, y - RAD);
					b = new Point(x + RAD, y + RAD);
					c = new Point(x + RAD, y);

				} else {

					x = x - 18;
					
					g.drawLine(x, y, x + LEG + RAD * 2, y);
					g.drawOval(x + LEG, y - RAD, RAD * 2, RAD * 2);
					g.drawLine(x + LEG + RAD - 1, y - RAD, x + LEG + RAD - 1, y
							+ RAD);
					g.drawLine(x + LEG + RAD + 1, y - RAD, x + LEG + RAD + 1, y
							+ RAD);

					a = new Point(x + LEG + RAD, y - RAD);
					b = new Point(x + LEG + RAD, y + RAD);
					c = new Point(x + LEG + RAD, y);

				}

				a.translate(-c.x, -c.y);
				b.translate(-c.x, -c.y);

				Point na = rotate(a, -45);
				Point nb = rotate(b, -45);
				na.translate(c);
				nb.translate(c);
				g.drawLine(na, nb);
			
			}

			@Override
			protected void fillShape(Graphics g) {
			}

		};

		label.setPreferredSize(0, 15);
		shapeFigure.setPreferredSize(0, 35);
		
		add(label);
		add(shapeFigure);
		
	}
	
	protected static Point rotate(Point p, int degrees) {
		double rad = Math.toRadians(degrees);
		double x = p.x;
		double y = p.y;
		int nx = (int) (x * Math.cos(rad) + y * Math.sin(rad));
		int ny = (int) (-x * Math.sin(rad) + y * Math.cos(rad));
		return new Point(nx, ny);
	}

	@Override
	public void deactivateFigure() {
		if(foregroundcolor != null)
			foregroundcolor.dispose();
	}

	@Override
	public void activateFigure() {
	}

	public void setTrackDirection(boolean isRight) {
		this.isRight = isRight;
		this.shapeFigure.repaint();
	}
	
	public boolean isRight() {
		return this.isRight;
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
		this.label.repaint();
	}

}
