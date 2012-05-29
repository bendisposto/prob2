package de.prob.visualization;

import java.util.Map;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DirectedMultigraph;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.RefType;

public class Visualization {

	public JGraphModelAdapter<ClassicalBMachine, RefType> modelAdapter;
	public DirectedMultigraph<ClassicalBMachine, RefType> graph;

	public Visualization(final ClassicalBModel model) {
		graph = model.getGraph();
		modelAdapter = new JGraphModelAdapter<ClassicalBMachine, RefType>(graph);
		init(model);
	}

	public void init(final ClassicalBModel model) {
		JFrame jf = new JFrame();

		JGraph jgraph = new JGraph(modelAdapter);

		VertexView.renderer = new MachineRenderer();

		jf.setSize(600, 600);

		ClassicalBMachine mainMachine = model.getMainMachine();
		JGraphLayout layout = new JGraphHierarchicalLayout(true);
		JGraphFacade f = new JGraphFacade(jgraph,
				new ClassicalBMachine[] { model.getMainMachine() });
		layout.run(f);

		Map nested = f.createNestedMap(true, true);
		GraphLayoutCache glc = jgraph.getGraphLayoutCache();
		glc.edit(nested);
		jf.getContentPane().add(jgraph);
		jf.pack();
		jf.setVisible(true);
	}
}
