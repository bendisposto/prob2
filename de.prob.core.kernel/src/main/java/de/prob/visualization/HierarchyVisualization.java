package de.prob.visualization;

import java.util.HashMap;

import javax.swing.JFrame;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;

import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Label;
import de.prob.model.representation.RefType;

public class HierarchyVisualization {

	public HierarchyVisualization(final AbstractModel model) {

		if (model == null) {
			throw new IllegalArgumentException("Model must not be null");
		}

		final JGraphXAdapter<String, RefType> graph = new JGraphXAdapter<String, RefType>(
				model.getGraph());

		final HashMap<mxCell, MachineConstants> constants = new HashMap<mxCell, MachineConstants>();
		final HashMap<String, Label> components = model.getComponents();

		final JFrame frame = new JFrame();
		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);

		final mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

		for (final mxCell cell : graph.getVertexToCellMap().values()) {
			final Object value = cell.getValue();
			if (value instanceof String) {
				final String elementName = (String) value;
				if (components.containsKey(elementName)) {
					final MachineConstants cons = new MachineConstants(
							components.get(elementName));
					constants.put(cell, cons);
					graphComponent.addCellOverlay(cell, new HTMLButtonOverlay(
							cons.getHtml(), cons.getWidth(), cons.getHeight()));
				}
			}
		}

		graph.getModel().beginUpdate();
		double x = 20, y = 20;
		for (final mxCell cell : graph.getVertexToCellMap().values()) {
			final MachineConstants cons = constants.get(cell);
			graph.getModel().setGeometry(cell,
					new mxGeometry(x, y, cons.getWidth(), cons.getHeight()));

			x += cons.getWidth();
			if (x > 200) {
				x = 20;
				y += 50;
			}
		}

		layout.execute(graph.getDefaultParent());
		graph.getModel().endUpdate();
	}
}
