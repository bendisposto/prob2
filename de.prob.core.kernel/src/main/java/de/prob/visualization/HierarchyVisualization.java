package de.prob.visualization;

import java.util.HashMap;

import javax.swing.JFrame;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.RefType;

public class HierarchyVisualization {

	HashMap<mxCell, MachineConstants> constants = new HashMap<mxCell, MachineConstants>();

	public HierarchyVisualization(final ClassicalBModel model) {
		JGraphXAdapter<ClassicalBMachine, RefType> graph = new JGraphXAdapter<ClassicalBMachine, RefType>(
				model.getGraph());

		JFrame frame = new JFrame();
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

		for (mxCell cell : graph.getVertexToCellMap().values()) {
			Object value = cell.getValue();
			if (value instanceof ClassicalBMachine) {
				ClassicalBMachine machine = (ClassicalBMachine) value;
				MachineConstants cons = new MachineConstants(machine);
				constants.put(cell, cons);
				graphComponent.addCellOverlay(cell,
						new HTMLButtonOverlay(cons.getHtml(), cons.getWidth(),
								cons.getHeight()));
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
