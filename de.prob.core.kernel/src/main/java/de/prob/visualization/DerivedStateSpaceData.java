package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.derived.DerivedStateId;

public class DerivedStateSpaceData extends AbstractData {

	@Override
	public Node addNode(final StateId id) {
		return addNode(id, -1);
	}

	@Override
	protected Node addNode(final StateId id, final int parentIndex) {
		List<String> vs = new ArrayList<String>();
		if (id instanceof DerivedStateId) {
			vs.add(((DerivedStateId) id).getLabel());
			vs.add(((DerivedStateId) id).getCount() + "");
		}

		Node node = new Node(id.getId(), id.getId(), parentIndex, vs);
		nodes.put(id.getId(), node);
		data.nodes.add(node);
		count++;
		return node;
	}

	@Override
	public Link addLink(final OpInfo op) {
		Node src = nodes.get(op.src);
		Node dest = nodes.get(op.dest);
		Link link = new Link(op.id, data.nodes.indexOf(src),
				data.nodes.indexOf(dest), op.getRep(), "#666");
		links.put(op.id, link);
		data.links.add(link);
		count++;
		return link;
	}

	@Override
	public int varSize() {
		return 2;
	}

	@Override
	public int getMode() {
		return 2;
	}
}
