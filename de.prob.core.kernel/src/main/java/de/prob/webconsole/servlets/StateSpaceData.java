package de.prob.webconsole.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpaceGraph;

public class StateSpaceData {

	private final Data data;
	private final Map<String, Node> nodes = new HashMap<String, Node>();
	private final Map<String, Link> links = new HashMap<String, Link>();
	private Data changes;

	public StateSpaceData() {
		data = new Data();
		changes = new Data();
	}

	private Node addNode(final StateId id, final int parentIndex) {
		Node node = new Node(id.getId(), id.getId(), parentIndex);
		nodes.put(id.getId(), node);
		data.nodes.add(node);
		return node;
	}

	public Node addNode(final StateId id) {
		return addNode(id, -1);
	}

	public Link addLink(final OpInfo op) {
		Node src = nodes.get(op.src);
		Node dest = nodes.get(op.dest);
		Link link = new Link(op.id, data.nodes.indexOf(src),
				data.nodes.indexOf(dest));
		links.put(op.id, link);
		data.links.add(link);
		return link;
	}

	public void addNewLinks(final StateSpaceGraph g, final List<OpInfo> ops) {
		for (OpInfo opInfo : ops) {
			if (!links.containsKey(opInfo.id)) {
				if (!nodes.containsKey(opInfo.src)) {
					Node newSrc = addNode(g.getVertex(opInfo.src));
					changes.nodes.add(newSrc);
				}

				if (!nodes.containsKey(opInfo.dest)) {
					Node newDest = addNode(g.getVertex(opInfo.dest),
							data.nodes.indexOf(nodes.get(opInfo.src)));
					changes.nodes.add(newDest);
				}

				Link newLink = addLink(opInfo);
				changes.links.add(newLink);
			}
		}
	}

	/**
	 * @return an Data Object containing the changes since the last time the
	 *         StateSpaceData was polled for data. The changes are returned and
	 *         deleted.
	 */
	public Data getChanges() {
		Data changes = this.changes;
		this.changes = new Data();
		return changes;
	}

	public boolean containsNode(final String id) {
		return nodes.containsKey(id);
	}

	public boolean containsLink(final String id) {
		return links.containsKey(id);
	}

	public Node getNode(final String id) {
		return nodes.get(id);
	}

	public Link getLink(final String id) {
		return links.get(id);
	}

	public boolean isEmpty() {
		return nodes.isEmpty() && links.isEmpty();
	}

	public int count() {
		return nodes.size() + links.size();
	}

	public Data getData() {
		return data;
	}

	private class Data {

		public List<Node> nodes = new ArrayList<Node>();
		public List<Link> links = new ArrayList<Link>();
	}

	private class Node {
		public String id;
		public String name;
		private final int parentIndex;

		public Node(final String id, final String name, final int parentIndex) {
			this.id = id;
			this.name = name;
			this.parentIndex = parentIndex;
		}
	}

	private class Link {
		public int source;
		public int target;
		public final String id;

		public Link(final String id, final int i, final int j) {
			source = i;
			target = j;
			this.id = id;
		}
	}

}
