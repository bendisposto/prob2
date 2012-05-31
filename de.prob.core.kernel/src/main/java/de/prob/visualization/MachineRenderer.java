package de.prob.visualization;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

		// String[][] data = new String[1 + m.constants().size()
		// + m.variables().size() + m.operations().size()][1];
		//
		// data[0][0] = m.name();
		// int row = 1;
		// for (ClassicalBEntity e : m.constants()) {
		// data[row++][0] = e.getIdentifier();
		// }
		// for (ClassicalBEntity e : m.variables()) {
		// data[row++][0] = e.getIdentifier();
		// }
		// for (Operation e : m.operations()) {
		// data[row++][0] = e.toString();
		// }
		//
		// JTable t = new JTable(data, new String[] { "M" });
		// t.setOpaque(true);
		// t.setBackground(Color.ORANGE);
		// t.setForeground(Color.BLACK);
		// t.setGridColor(Color.WHITE);

		JPanel box = createVertexRep(m);

		Rectangle2D oldBounds = this.view.getBounds();
		oldBounds.setRect(oldBounds.getX(), oldBounds.getY(), box.getWidth(),
				box.getHeight());
		this.view.setBounds(oldBounds);
		System.out.println(this.view.getBounds());
		return box;
	}

	public JPanel createVertexRep(final ClassicalBMachine m) {
		JPanel box = new JPanel(new GridLayout(calcNumOfRows(m), 1));
		JLabel name = new JLabel(m.name());
		box.add(name);
		if (!m.variables().isEmpty()) {
			JPanel variables = new JPanel(new FlowLayout());
			variables.add(new JLabel("Variables"));
			for (ClassicalBEntity var : m.variables()) {
				variables.add(new JLabel(" " + var.getIdentifier()));
			}
			box.add(variables);
		}
		if (!m.constants().isEmpty()) {
			JPanel constants = new JPanel(new GridLayout(1 + m.constants()
					.size(), 1));
			constants.add(new JLabel("Constants"));
			for (ClassicalBEntity constant : m.constants()) {
				constants.add(new JLabel(" " + constant.getIdentifier()));
			}
			box.add(constants);
		}
		if (!m.operations().isEmpty()) {
			JPanel operations = new JPanel(new GridLayout(1 + m.operations()
					.size(), 1));
			operations.add(new JLabel("Operations"));
			for (Operation op : m.operations()) {
				operations.add(new JLabel(" " + op.toString()));
			}
		}
		box.setSize(new Dimension(100, (1 + m.variables().size()
				+ m.constants().size() + m.operations().size()) * 25));
		return box;
	}

	public int calcNumOfRows(final ClassicalBMachine m) {
		int nr = 1;
		if (!m.variables().isEmpty()) {
			nr++;
		}
		if (!m.constants().isEmpty()) {
			nr++;
		}
		if (!m.operations().isEmpty()) {
			nr++;
		}
		return nr;
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
