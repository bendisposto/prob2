package de.prob.visualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;

import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpaceGraph;
import de.prob.statespace.derived.AbstractDerivedStateSpace;
import de.prob.statespace.derived.DerivedOp;
import de.prob.statespace.derived.DerivedStateId;

public class DerivedStateSpaceData extends AbstractData {

	public int varSize = 2;
	private final AbstractDerivedStateSpace space;
	private final Map<String, Transformer> styles = new HashMap<String, Transformer>();

	public DerivedStateSpaceData(final AbstractDerivedStateSpace space) {
		this.space = space;

		styles.put("orange", new Transformer("").set("fill", "#E6906E"));
		styles.put("red", new Transformer("").set("fill", "#B56C6C"));
		styles.put("blue", new Transformer("").set("fill", "#5684A0"));
		styles.put("green", new Transformer("").set("fill", "#799C79"));

		styles.put("lightgray", new Transformer("").set("stroke", "#B2B2B2"));
		styles.put("black", new Transformer("").set("stroke", "#000"));

		styles.put("dashed", new Transformer("").set("stroke-dasharray", "2,2"));
	}

	@Override
	public Node addNode(final StateId id) {
		return addNode(id, -1);
	}

	@Override
	protected Node addNode(final StateId id, final int parentIndex) {
		List<String> vs = new ArrayList<String>();
		if (id instanceof DerivedStateId) {
			vs.addAll(((DerivedStateId) id).getLabels());
			vs.add(((DerivedStateId) id).getCount() + "");
		}
		if (vs.size() > varSize) {
			varSize = vs.size();
		}

		Node node = new Node(id.getId(), id.getId(), parentIndex, vs, "unknown");
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
	public void addNewLinks(final StateSpaceGraph graph,
			final List<? extends OpInfo> newOps) {
		updateTransformers();
		super.addNewLinks(graph, newOps);
	}

	@Override
	public int varSize() {
		return varSize;
	}

	@Override
	public void updateTransformers() {
		Map<String, Set<DerivedStateId>> nCs = space.getNodeColors();
		for (Entry<String, Set<DerivedStateId>> e : nCs.entrySet()) {
			Set<DerivedStateId> v = e.getValue();
			if (!v.isEmpty()) {
				List<String> toSelect = new ArrayList<String>();
				for (DerivedStateId derivedStateId : v) {
					toSelect.add("#s" + derivedStateId.getId());
				}
				Transformer transformer = styles.get(e.getKey());
				transformer.updateSelector(Joiner.on(",").join(toSelect));
				if (!data.styling.contains(transformer)) {
					addStyling(transformer);
				} else {
					count++;
				}
			}
		}

		Map<String, Set<DerivedOp>> tCs = space.getTransColor();
		for (Entry<String, Set<DerivedOp>> e : tCs.entrySet()) {
			Set<DerivedOp> v = e.getValue();
			if (!v.isEmpty()) {
				List<String> toSelect = new ArrayList<String>();
				for (DerivedOp derivedOp : v) {
					toSelect.add("#t" + derivedOp.getId());
				}
				Transformer transformer = styles.get(e.getKey());
				transformer.updateSelector(Joiner.on(",").join(toSelect));
				if (!data.styling.contains(transformer)) {
					addStyling(transformer);
				} else {
					count++;
				}
			}
		}

		Set<DerivedOp> dashed = space.getTransStyle().get("dashed");
		if (dashed != null && !dashed.isEmpty()) {
			List<String> toSelect = new ArrayList<String>();
			for (DerivedOp derivedOp : dashed) {
				toSelect.add("#t" + derivedOp.getId());
			}
			Transformer transformer = styles.get("dashed");
			transformer.updateSelector(Joiner.on(",").join(toSelect));
			if (!data.styling.contains(transformer)) {
				addStyling(transformer);
			} else {
				count++;
			}
		}

	}

	@Override
	public void closeData() {
		// TODO Auto-generated method stub

	}

}
