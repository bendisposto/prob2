package de.prob.visualization;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.derived.DerivedOp;
import de.prob.statespace.derived.DerivedStateId;

public class DerivedStateSpaceData extends AbstractData {

	List<String> dotted = new ArrayList<String>();

	@Override
	public Node addNode(final StateId id) {
		return addNode(id, -1);
	}

	@Override
	protected Node addNode(final StateId id, final int parentIndex) {
		List<String> vs = new ArrayList<String>();
		if (id instanceof DerivedStateId) {
			vs.add(((DerivedStateId) id).getLabel());
		}

		Node node = new Node(id.getId(), id.getId(), parentIndex, vs);
		nodes.put(id.getId(), node);
		data.nodes.add(node);
		count++;
		return node;
	}

	@Override
	public Link addLink(final OpInfo op) {
		String color = "#666";
		if (op instanceof DerivedOp) {
			color = ((DerivedOp) op).getColor();
			String style = ((DerivedOp) op).getStyle();
			if (style.equals("dotted")) {
				dotted.add("#arc" + op.getId());
			}
		}

		Node src = nodes.get(op.src);
		Node dest = nodes.get(op.dest);
		Link link = new Link(op.id, data.nodes.indexOf(src),
				data.nodes.indexOf(dest), op.getRep(), color);
		links.put(op.id, link);
		data.links.add(link);
		count++;
		return link;
	}

	@Override
	public int varSize() {
		return 1;
	}

	@Override
	public List<Selection> getStyling() {
		String selector = Joiner.on(',').join(dotted);
		userOptions.add(new Selection(selector).attr("stroke-dasharray",
				"10,10"));
		dotted = new ArrayList<String>();
		return super.getStyling();
	}

}
