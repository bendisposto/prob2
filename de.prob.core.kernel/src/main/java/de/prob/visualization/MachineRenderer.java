package de.prob.visualization;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import de.prob.model.classicalb.ClassicalBEntity;
import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.representation.Operation;

@SuppressWarnings("serial")
public class MachineRenderer extends VertexRenderer {

	@Override
	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
		this.view = (VertexView) view;
		DefaultGraphCell cell = (DefaultGraphCell) this.view.getCell();
		ClassicalBMachine m = (ClassicalBMachine) cell.getUserObject();

		String[][] data = new String[1 + m.constants().size()
				+ m.variables().size() + m.operations().size()][1];

		data[0][0] = m.name();
		int row = 1;
		for (ClassicalBEntity e : m.constants()) {
			data[row++][0] = e.getIdentifier();
		}
		for (ClassicalBEntity e : m.variables()) {
			data[row++][0] = e.getIdentifier();
		}
		for (Operation e : m.operations()) {
			data[row++][0] = e.toString();
		}

		JTable t = new JTable(data, new String[] { "M" });
		t.setBackground(Color.WHITE);
		t.setForeground(Color.black);
		return t;

	}
}
