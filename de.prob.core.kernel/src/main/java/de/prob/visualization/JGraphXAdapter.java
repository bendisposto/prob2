package de.prob.visualization;

import java.util.HashMap;

import javax.swing.JFrame;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class JGraphXAdapter<V, E> extends mxGraph {
	private final DirectedMultigraph<V, E> graphT;
	private final HashMap<V, mxCell> vertexToCellMap = new HashMap<V, mxCell>();
	private final HashMap<E, mxCell> edgeToCellMap = new HashMap<E, mxCell>();
	private final HashMap<mxCell, V> cellToVertexMap = new HashMap<mxCell, V>();
	private final HashMap<mxCell, E> cellToEdgeMap = new HashMap<mxCell, E>();

	/*
	 * CONSTRUCTOR
	 */

	public JGraphXAdapter(final DirectedMultigraph<V, E> graphT) {
		super();
		this.graphT = graphT;

		insertJGraphT(graphT);

	}

	/*
	 * METHODS
	 */
	public void addJGraphTVertex(final V vertex) {
		getModel().beginUpdate();
		try {
			mxCell cell = new mxCell(vertex);
			cell.setVertex(true);
			cell.setId(null);
			addCell(cell, defaultParent);
			vertexToCellMap.put(vertex, cell);
			cellToVertexMap.put(cell, vertex);
		} finally {
			getModel().endUpdate();
		}
	}

	public void addJGraphTEdge(final E edge) {
		getModel().beginUpdate();
		try {
			V source = graphT.getEdgeSource(edge);
			V target = graphT.getEdgeTarget(edge);
			mxCell cell = new mxCell(edge);
			cell.setEdge(true);
			cell.setId(null);
			cell.setGeometry(new mxGeometry());
			cell.getGeometry().setRelative(true);
			addEdge(cell, defaultParent, vertexToCellMap.get(source),
					vertexToCellMap.get(target), null);
			edgeToCellMap.put(edge, cell);
			cellToEdgeMap.put(cell, edge);
		} finally {
			getModel().endUpdate();
		}
	}

	public HashMap<V, mxCell> getVertexToCellMap() {
		return vertexToCellMap;
	}

	public HashMap<E, mxCell> getEdgeToCellMap() {
		return edgeToCellMap;
	}

	public HashMap<mxCell, E> getCellToEdgeMap() {
		return cellToEdgeMap;
	}

	public HashMap<mxCell, V> getCellToVertexMap() {
		return cellToVertexMap;
	}

	@Override
	public String toString() {
		return getAllEdges(new Object[0]).toString();
	}

	/*
	 * PRIVATE METHODS
	 */

	private void insertJGraphT(final Graph<V, E> graphT) {
		getModel().beginUpdate();
		try {
			for (V vertex : graphT.vertexSet()) {
				addJGraphTVertex(vertex);
			}
			for (E edge : graphT.edgeSet()) {
				addJGraphTEdge(edge);
			}
		} finally {
			getModel().endUpdate();
		}
	}

	/*
	 * MAIN METHOD
	 */
	public static void main(final String[] args) {
		// create a JGraphT graph
		DirectedMultigraph<String, DefaultEdge> g = new DirectedMultigraph<String, DefaultEdge>(
				DefaultEdge.class);

		// add some sample data (graph manipulated via JGraphT)
		g.addVertex("v1");
		g.addVertex("v2");
		g.addVertex("v3");
		g.addVertex("v4");
		g.addVertex("v5");

		g.addEdge("v1", "v2");
		g.addEdge("v1", "v3");
		g.addEdge("v2", "v4");
		g.addEdge("v1", "v5");

		JGraphXAdapter<String, DefaultEdge> graph = new JGraphXAdapter<String, DefaultEdge>(
				g);

		JFrame frame = new JFrame();
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);

		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

		for (mxCell cell : graph.getVertexToCellMap().values()) {
			graphComponent.addCellOverlay(cell, new HTMLButtonOverlay(
					"<html>Moo<br>Bar</html>", 40, 40));
		}

		// mxGraphHierarchyModel model = new mxGraphHierarchyModel(layout, null,
		// null, false, false, false);

		graph.getModel().beginUpdate();
		double x = 20, y = 20;
		for (mxCell cell : graph.getVertexToCellMap().values()) {
			graph.getModel().setGeometry(cell, new mxGeometry(x, y, 40, 40));

			x += 90;
			if (x > 200) {
				x = 20;
				y += 50;
			}
		}

		layout.execute(graph.getDefaultParent());
		graph.getModel().endUpdate();

	}
}
