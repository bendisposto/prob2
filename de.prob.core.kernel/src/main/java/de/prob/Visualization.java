package de.prob;

import javax.swing.JFrame;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.RefType;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class Visualization {
	public Visualization(
			final DirectedSparseMultigraph<ClassicalBMachine, RefType> graph) {
		Layout l = new FRLayout(graph);

		VisualizationViewer vv = new VisualizationViewer(l);
		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<ClassicalBMachine>());
		vv.getRenderContext().setEdgeLabelTransformer(
				new ToStringLabeller<RefType>());
		JFrame jf = new JFrame();
		jf.getContentPane().add(vv);
		jf.pack();
		jf.show();
	}
}
