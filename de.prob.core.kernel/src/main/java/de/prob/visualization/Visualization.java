package de.prob.visualization;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DirectedMultigraph;

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
		HierarchyGraph jgraph = new HierarchyGraph(modelAdapter, model);
		jf.getContentPane().add(jgraph);
		jf.setSize(600, 600);

		ClassicalBMachine mainMachine = model.getMainMachine();
		positionVertexAt(mainMachine, 300, 40);

		List<ClassicalBMachine> vertices = adjacentVertices(mainMachine);
		int numOfVertices = vertices.size();
		int i = 0;
		for (ClassicalBMachine classicalBMachine : vertices) {
			positionVertexAt(classicalBMachine, 30 + 600 / numOfVertices * i,
					400);
			i++;
		}

		jf.pack();
		jf.setVisible(true);
	}

	private List<ClassicalBMachine> adjacentVertices(
			final ClassicalBMachine vertex) {
		List<ClassicalBMachine> vertices = new ArrayList<ClassicalBMachine>();

		Set<RefType> outgoingEdges = graph.outgoingEdgesOf(vertex);
		for (RefType edge : outgoingEdges) {
			vertices.add(graph.getEdgeTarget(edge));
		}

		return vertices;
	}

	private void positionVertexAt(final Object vertex, final int x, final int y) {
		DefaultGraphCell cell = modelAdapter.getVertexCell(vertex);
		AttributeMap attr = cell.getAttributes();
		System.out.println(cell);
		System.out.println(attr);
		Rectangle2D b = GraphConstants.getBounds(attr);

		b.setFrame(x, y, b.getWidth(), b.getHeight());
		GraphConstants.setBounds(attr, b);

		// GraphConstants
		// .setBounds(attr, new Rectangle2D(x, y, b.width, b.height));

		Map<DefaultGraphCell, AttributeMap> cellAttr = new HashMap<DefaultGraphCell, AttributeMap>();
		cellAttr.put(cell, attr);
		modelAdapter.edit(cellAttr, null, null, null);
	}

}
