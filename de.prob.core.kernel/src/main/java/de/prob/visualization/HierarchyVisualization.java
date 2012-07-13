package de.prob.visualization;

import java.util.HashMap;

import javax.swing.JFrame;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.RefType;

public class HierarchyVisualization {

	public HierarchyVisualization(final AbstractModel model) {
		JGraphXAdapter<String, RefType> graph = new JGraphXAdapter<String, RefType>(
				model.getGraph());

		final HashMap<mxCell, MachineConstants> constants = new HashMap<mxCell, MachineConstants>();
		final HashMap<String, AbstractElement> components = model
				.getComponents();

		JFrame frame = new JFrame();
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

		for (mxCell cell : graph.getVertexToCellMap().values()) {
			Object value = cell.getValue();
			if (value instanceof String) {
				String elementName = (String) value;
				if (components.containsKey(elementName)) {
					MachineConstants cons = new MachineConstants(
							components.get(elementName));
					constants.put(cell, cons);
					graphComponent.addCellOverlay(cell, new HTMLButtonOverlay(
							cons.getHtml(), cons.getWidth(), cons.getHeight()));
				}
			}
		}

		graph.getModel().beginUpdate();
		double x = 20, y = 20;
		for (mxCell cell : graph.getVertexToCellMap().values()) {
			MachineConstants cons = constants.get(cell);
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
