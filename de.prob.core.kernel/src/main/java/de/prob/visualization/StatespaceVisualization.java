package de.prob.visualization;

import javax.swing.JFrame;

import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

public class StatespaceVisualization {

	public StatespaceVisualization(final StateSpace s) {
		JGraphXAdapter<StateId, OpInfo> graph = new JGraphXAdapter<StateId, OpInfo>(
				s.getGraph());

		JFrame frame = new JFrame();
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);

		mxOrganicLayout layout = new mxOrganicLayout(graph);

		graph.getModel().beginUpdate();
		double x = 20, y = 20;
		for (mxCell cell : graph.getVertexToCellMap().values()) {
			graph.getModel().setGeometry(cell, new mxGeometry(x, y, 40, 40));

			x += 20;
			if (x > 200) {
				x = 20;
				y += 50;
			}
		}

		layout.execute(graph.getDefaultParent());
		graph.getModel().endUpdate();
	}
}
