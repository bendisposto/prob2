package de.prob.model.representation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;

/**
 * A simple graph implementation intended to display the relationships between
 * the components in a model. The nodes in the graph are represented as the
 * string element name of the component and the edges are the type of
 * relationship as defined by {@link ERefType}. It is a multigraph, which means
 * that there can be multiple edges between two nodes.
 * 
 * @author joy
 * 
 */
public class DependencyGraph {
	/**
	 * RefType is used for both ClassicalBModels and EventBModels
	 * 
	 * ClassicalB: SEES, USES, REFINES, INCLUDES, IMPORTS EventB: SEES, REFINES,
	 * EXTENDS
	 * 
	 * @author joy
	 * 
	 */
	public enum ERefType {
		SEES, USES, REFINES, INCLUDES, IMPORTS, EXTENDS
	}

	public class Node {
		final String elementName;
		List<Edge> outEdges = new ArrayList<Edge>();

		public Node(final String elementName) {
			this.elementName = elementName;
		}

		public String getElementName() {
			return elementName;
		}

		public List<Edge> getOutEdges() {
			return outEdges;
		}

		/**
		 * @param edge
		 *            to be added.
		 */
		public void addEdge(final Edge edge) {
			outEdges.add(edge);
		}

		@Override
		public boolean equals(final Object that) {
			if (this == that) {
				return true;
			}
			if (that instanceof Node) {
				return getElementName().equals(((Node) that).getElementName());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getElementName().hashCode();
		}
	}

	public class Edge {
		Node from;
		Node to;
		ERefType relationship;

		public Edge(final Node from, final Node to, final ERefType relationship) {
			this.from = from;
			this.to = to;
			this.relationship = relationship;
		}

		public Node getFrom() {
			return from;
		}

		public Node getTo() {
			return to;
		}

		public ERefType getRelationship() {
			return relationship;
		}

		@Override
		public boolean equals(final Object that) {
			if (this == that) {
				return true;
			}
			if (that instanceof Edge) {
				return getFrom().equals(((Edge) that).getFrom())
						&& getTo().equals(((Edge) that).getTo())
						&& getRelationship().equals(
								((Edge) that).getRelationship());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(from, to, relationship);
		}
	}

	Map<String, Node> graph = new HashMap<String, DependencyGraph.Node>();

	/**
	 * @param element
	 *            element to be added to the graph it is has not already been
	 *            added;
	 */
	public void addVertex(final String element) {
		if (!graph.containsKey(element)) {
			graph.put(element, new Node(element));
		}
	}

	/**
	 * @param element
	 *            to be found in the graph
	 * @return whether or not the element has been added to the graph
	 */
	public boolean containsVertex(final String element) {
		return graph.containsKey(element);
	}

	public Set<String> getVertices() {
		return new HashSet<String>(graph.keySet());
	}

	public Set<Edge> getEdges() {
		HashSet<Edge> set = new HashSet<Edge>();
		for (Node node : graph.values()) {
			set.addAll(node.getOutEdges());
		}
		return set;
	}

	/**
	 * 
	 * @param from
	 *            source element
	 * @param to
	 *            destination element
	 * @param relationship
	 *            relationship between the two elements
	 */
	public void addEdge(final String from, final String to,
			final ERefType relationship) {
		if (!graph.containsKey(from)) {
			addVertex(from);
		}
		if (!graph.containsKey(to)) {
			addVertex(to);
		}
		Node f = graph.get(from);
		Node t = graph.get(to);
		Edge e = new Edge(f, t, relationship);
		f.addEdge(e);
	}

	/**
	 * @param from
	 *            source node
	 * @param to
	 *            destination node
	 * @return the list of relationships between the two elements.
	 */
	public List<ERefType> getRelationships(final String from, final String to) {
		if (!graph.containsKey(from)) {
			throw new IllegalArgumentException("Element " + from
					+ " is not in graph.");
		}
		if (!graph.containsKey(to)) {
			throw new IllegalArgumentException("Element " + to
					+ " is not in graph.");
		}
		Node f = graph.get(from);
		Node t = graph.get(to);

		List<Edge> edgeSet = f.getOutEdges();
		List<ERefType> relationships = new ArrayList<DependencyGraph.ERefType>();
		for (Edge edge : edgeSet) {
			if (edge.getFrom().equals(f) && edge.getTo().equals(t)) {
				relationships.add(edge.getRelationship());
			}
		}

		return relationships;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(graph.keySet().toString());
		sb.append(", ");

		List<String> s = new ArrayList<String>();
		for (Entry<String, Node> entry : graph.entrySet()) {
			List<Edge> outEdges = entry.getValue().getOutEdges();
			for (Edge edge : outEdges) {
				s.add(edge.getRelationship().toString() + "=("
						+ edge.getTo().getElementName() + ","
						+ edge.getFrom().getElementName() + ")");
			}
		}
		sb.append(s.toString());
		sb.append(")");
		return sb.toString();
	}

}
