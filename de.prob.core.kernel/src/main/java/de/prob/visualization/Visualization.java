package de.prob.visualization;

import java.util.Map;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
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
		jgraph.setGridSize(20);
		VertexView.renderer = new MachineRenderer();
		jf.setSize(600, 600);

		ClassicalBMachine mainMachine = model.getMainMachine();
		JGraphLayout layout = new JGraphHierarchicalLayout(true);
		JGraphFacade f = new JGraphFacade(jgraph,
				new ClassicalBMachine[] { model.getMainMachine() });
		layout.run(f);
		System.out.println(f.getAttributes());

		Map nested = f.createNestedMap(true, true);
		System.out.println(nested);
		GraphLayoutCache glc = jgraph.getGraphLayoutCache();
		change(nested, glc);
		glc.edit(nested);
		jf.getContentPane().add(jgraph);
		jf.pack();
		jf.setVisible(true);
	}

	public void change(final Map nested, final GraphLayoutCache glc) {
		CellView[] allViews = glc.getCellViews();
		for (CellView cellView : allViews) {
			if (cellView instanceof VertexView) {
				DefaultGraphCell cell = (DefaultGraphCell) cellView.getCell();
				if (cell.getUserObject() instanceof ClassicalBMachine) {
					ClassicalBMachine m = (ClassicalBMachine) cell
							.getUserObject();
					int width = 100;
					int height = 1 + m.variables().size()
							+ m.constants().size() + m.operations().size();
					Object object = nested.get(cell);
					System.out.println(((java.util.Hashtable) object)
							.contains("bounds"));
					System.out.println(object.getClass());
					System.out.println(m.name());
					System.out.println(nested.containsKey(cell));
					System.out.println(nested.keySet());
				}
			}
		}
	}
}
