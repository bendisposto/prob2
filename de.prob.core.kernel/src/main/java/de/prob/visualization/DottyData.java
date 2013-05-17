package de.prob.visualization;

import java.util.List;
import java.util.Random;

import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpaceGraph;
import de.prob.statespace.derived.AbstractDottyGraph;

public class DottyData extends AbstractData {

	private final AbstractDottyGraph space;
	private final Random rand;

	public DottyData(final AbstractDottyGraph space) {
		this.space = space;
		data.content = space.getContent();
		changes.content = space.getContent();
		rand = new Random();
		count = (rand.nextInt(13) % 5000);
	}

	@Override
	public Node addNode(final StateId id) {
		return null;
	}

	@Override
	protected Node addNode(final StateId id, final int parentIndex) {
		return null;
	}

	@Override
	public Link addLink(final OpInfo id) {
		return null;
	}

	@Override
	public void addNewLinks(final StateSpaceGraph graph,
			final List<? extends OpInfo> newOps) {
		data.content = space.getContent();
		changes.content = space.getContent();
		count = (count + rand.nextInt(8) % 4500);
	}

	@Override
	public int varSize() {
		return 0;
	}

	@Override
	public void updateTransformers() {
	}

	@Override
	public Data getChanges() {
		Data d = super.getChanges();
		changes.content = space.getContent();
		return d;
	}

}
