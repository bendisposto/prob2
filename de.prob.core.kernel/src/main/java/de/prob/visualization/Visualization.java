package de.prob.visualization;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
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

	@SuppressWarnings("rawtypes")
	public void init(final ClassicalBModel model) {
		JFrame jf = new JFrame();

		JGraph jgraph = new JGraph(modelAdapter);
		jgraph.setGridSize(20);
		VertexView.renderer = new MachineRenderer();
		jf.setSize(600, 600);

		JGraphLayout layout = new JGraphHierarchicalLayout(true);
		JGraphFacade f = new JGraphFacade(jgraph,
				new ClassicalBMachine[] { model.getMainMachine() });
		layout.run(f);

		Map nested = f.createNestedMap(true, true);
		GraphLayoutCache glc = jgraph.getGraphLayoutCache();
		change(nested, glc);
		glc.edit(nested);
		jf.getContentPane().add(jgraph);
		jf.pack();
		jf.setVisible(true);
	}

	@SuppressWarnings("rawtypes")
	public void change(final Map nested, final GraphLayoutCache glc) {
		CellView[] allViews = glc.getCellViews();
		for (CellView cellView : allViews) {
			if (cellView instanceof VertexView) {
				DefaultGraphCell cell = (DefaultGraphCell) cellView.getCell();
				if (cell.getUserObject() instanceof ClassicalBMachine) {
					ClassicalBMachine m = (ClassicalBMachine) cell
							.getUserObject();
					MachineConstants cons = new MachineConstants(m);
					if (nested != null) {
						Hashtable hashtable = (Hashtable) nested.get(cell);
						double y = ((SerializableRectangle2D) hashtable
								.get("bounds")).getY();
						((SerializableRectangle2D) hashtable.get("bounds"))
								.setWidth(cons.calculateWidth());
						((SerializableRectangle2D) hashtable.get("bounds"))
								.setHeight(cons.calculateHeight());
						((SerializableRectangle2D) hashtable.get("bounds"))
								.setY(y * 3);
					}
				}
			}
		}
	}
}
