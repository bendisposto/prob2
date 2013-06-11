package de.prob.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpaceGraph;

public abstract class AbstractData {

	protected final Data data;
	protected final Map<String, Node> nodes = new HashMap<String, Node>();
	protected final Map<String, Link> links = new HashMap<String, Link>();
	protected int count = 0;
	protected Data changes;
	protected int mode;

	public AbstractData() {
		data = new Data();
		changes = new Data();
	}

	public abstract Node addNode(StateId id);

	protected abstract Node addNode(StateId id, int parentIndex);

	public abstract Link addLink(OpInfo id);

	public abstract int varSize();

	public int getMode() {
		return mode;
	}

	public void addNewLinks(final StateSpaceGraph graph,
			final List<? extends OpInfo> newOps) {
		for (OpInfo opInfo : newOps) {
			if (!links.containsKey(opInfo.id)) {
				if (!nodes.containsKey(opInfo.src)) {
					Node newSrc = addNode(graph.getVertex(opInfo.src));
					changes.nodes.add(newSrc);
				}

				if (!nodes.containsKey(opInfo.dest)) {
					Node newDest = addNode(graph.getVertex(opInfo.dest),
							data.nodes.indexOf(nodes.get(opInfo.src)));
					changes.nodes.add(newDest);
				}

				Link newLink = addLink(opInfo);
				changes.links.add(newLink);
			}
		}
	}

	public void addStyling(final Transformer s) {
		data.styling.add(s);
		changes.styling.add(s);
		count++;
	}

	public void setReset(final boolean value) {
		data.reset = value;
	}

	public boolean getReset() {
		return data.reset;
	}

	public abstract void updateTransformers();

	protected class Data {

		public List<Node> nodes = new ArrayList<Node>();
		public List<Link> links = new ArrayList<Link>();

		public List<Transformer> styling = new ArrayList<Transformer>();
		public boolean reset = false;
		public String content = "";
	}

	protected class Node {
		public String id;
		public String name;
		public final int parentIndex;
		public final List<String> vars;
		public Object invOk;

		public Node(final String id, final String name, final int parentIndex,
				final List<String> vars, final Object invOk) {
			this.id = id;
			this.name = name;
			this.parentIndex = parentIndex;
			this.vars = vars;
			this.invOk = invOk;
		}
	}

	protected class Link {
		public int source;
		public int target;
		public final String id;
		public final String name;
		public final String color;

		public Link(final String id, final int i, final int j,
				final String name, final String color) {
			source = i;
			target = j;
			this.id = id;
			this.name = name;
			this.color = color;
		}
	}

	public Data getData() {
		return data;
	}

	public int count() {
		return count;
	}

	/**
	 * @return an Data Object containing the changes since the last time the
	 *         StateSpaceData was polled for data. The changes are returned and
	 *         deleted.
	 */
	public Data getChanges() {
		Data changes = this.changes;
		this.changes = new Data();
		this.changes.styling.addAll(data.styling);
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

	public void setMode(final int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return data.content;
	}

	public List<Transformer> getStyling() {
		return data.styling;
	}

	public abstract void closeData();

}
