package de.prob.webconsole.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;

public class StateSpaceData {

	private final Data data;
	private final Map<String, Node> nodes = new HashMap<String, Node>();
	private final Map<String, Link> links = new HashMap<String, Link>();

	public StateSpaceData() {
		data = new Data();
	}

	public void addNode(final int x, final int y, final StateId id) {
		Node node = new Node(x, y, id.getId(), id.getId());
		nodes.put(id.getId(), node);
		data.nodes.add(node);
	}

	public void addLink(final OpInfo op) {
		Node src = nodes.get(op.src);
		Node dest = nodes.get(op.dest);
		Link link = new Link(op.id, data.nodes.indexOf(src),
				data.nodes.indexOf(dest));
		links.put(op.id, link);
		data.links.add(link);
	}

	public void addNewLinks(final StateSpace s, final List<OpInfo> ops) {
		for (OpInfo opInfo : ops) {
			Node src = nodes.get(opInfo.src);
			if (src == null) {
				addNode(0, 0, s.getVertex(opInfo.src));
			}

			Node dest = nodes.get(opInfo.dest);
			if (dest == null) {
				addNode(src.x, src.y, s.getVertex(opInfo.dest));
			}

			addLink(opInfo);
		}
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
		public int x;
		public int y;
		public String id;
		public String name;

		public Node(final int x, final int y, final String id, final String name) {
			this.x = x;
			this.y = y;
			this.id = id;
			this.name = name;
		}
	}

	private class Link {
		public int source;
		public int target;
		public final String id;

		public Link(final String id, final int source, final int target) {
			this.source = source;
			this.target = target;
			this.id = id;
		}
	}

}
