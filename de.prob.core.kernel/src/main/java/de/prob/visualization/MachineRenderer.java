package de.prob.visualization;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.prob.model.classicalb.ClassicalBEntity;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.representation.Operation;

@SuppressWarnings("serial")
public class MachineRenderer extends VertexRenderer {

	@Override
	public Component getRendererComponent(final JGraph graph,
			final CellView view, final boolean sel, final boolean focus,
			final boolean preview) {
		this.view = (VertexView) view;
		DefaultGraphCell cell = (DefaultGraphCell) this.view.getCell();
		ClassicalBMachine m = (ClassicalBMachine) cell.getUserObject();

		JPanel box = createVertexRep(m);

		Rectangle2D oldBounds = this.view.getBounds();
		oldBounds.setRect(oldBounds.getX(), oldBounds.getY(), box.getWidth(),
				box.getHeight());
		this.view.setBounds(oldBounds);
		return box;
	}

	public JPanel createVertexRep(final ClassicalBMachine m) {
		MachineConstants cons = new MachineConstants(m);

		JPanel box = new JPanel(new GridBagLayout());
		int width = cons.calculateWidth();
		box.setSize(new Dimension(width, cons.calculateHeight()));
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		box.add(new JLabel(m.name(), CENTER), c);
		int row = 1;
		if (!m.variables().isEmpty()) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(new JLabel("Variables:", CENTER), c);
			row++;
			int maxIntValue = cons.getLargestVar();
			JPanel vars = new JPanel(new FlowLayout());
			int col = 0;
			for (ClassicalBEntity var : m.variables()) {
				vars.add(new JLabel(var.getIdentifier()));
				col++;
				if (col * maxIntValue > 100) {
					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = 0;
					c.gridy = row;
					box.add(vars, c);
					vars = new JPanel(new FlowLayout());
					col = 0;
					row++;
				}
			}
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(vars, c);
			row++;
		}
		if (!m.constants().isEmpty()) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(new JLabel("Constants:", CENTER), c);
			row++;
			int maxIntValue = cons.getLargestConstant();
			JPanel cs = new JPanel(new FlowLayout());
			int col = 0;
			for (ClassicalBEntity constant : m.constants()) {
				cs.add(new JLabel(constant.getIdentifier()));
				col++;
				if (col * maxIntValue > 100) {
					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = 0;
					c.gridy = row;
					box.add(cs, c);
					cs = new JPanel(new FlowLayout());
					col = 0;
					row++;
				}
			}
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(cs, c);
			row++;
		}
		if (!m.operations().isEmpty()) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(new JLabel("Operations:", CENTER), c);
			row++;
			int maxIntValue = cons.getLargestOp();
			int col = 0;
			JPanel ops = new JPanel(new FlowLayout());
			for (Operation op : m.operations()) {
				ops.add(new JLabel(op.toString()));
				col++;
				if (col * maxIntValue > 100) {
					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = 0;
					c.gridy = row;
					box.add(ops, c);
					ops = new JPanel(new FlowLayout());
					col = 0;
					row++;
				}
			}
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = row;
			box.add(ops, c);
			row++;
		}

		return box;
	}

	@Override
	public Point2D getPerimeterPoint(final VertexView view,
			final Point2D source, final Point2D p) {
		// TODO Auto-generated method stub
		Rectangle2D bounds = view.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}

	public void update(final GraphLayoutCache cache) {
		this.view.update(cache);
	}

}
