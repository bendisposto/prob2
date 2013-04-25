package de.prob.statespace.derived;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.prob.animator.command.ICommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.StateSpaceGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public abstract class AbstractDerivedStateSpace extends StateSpaceGraph
		implements IStatesCalculatedListener, IStateSpace {

	protected final StateSpace stateSpace;
	private int idGenerator;

	public AbstractDerivedStateSpace(final IStateSpace stateSpace) {
		super(new DirectedSparseMultigraph<StateId, OpInfo>());
		if (stateSpace instanceof StateSpace) {
			this.stateSpace = (StateSpace) stateSpace;
		} else if (stateSpace instanceof AbstractDerivedStateSpace) {
			this.stateSpace = ((AbstractDerivedStateSpace) stateSpace)
					.getStateSpace();
		} else {
			throw new UnsupportedOperationException(
					"Could not create AbstractDerivedStateSpace because the instance of IStateSpace was not recognized");
		}
		idGenerator = 0;
	}

	Set<IStatesCalculatedListener> listeners = new HashSet<IStatesCalculatedListener>();

	@Override
	public abstract void newTransitions(final IStateSpace s,
			final List<? extends OpInfo> newOps);

	@Override
	public void notifyStateSpaceChange(final List<? extends OpInfo> newOps) {
		for (IStatesCalculatedListener l : listeners) {
			l.newTransitions(this, newOps);
		}
	}

	@Override
	public void registerStateSpaceListener(final IStatesCalculatedListener l) {
		listeners.add(l);
	}

	@Override
	public void deregisterStateSpaceListener(final IStatesCalculatedListener l) {
		listeners.remove(l);
	}

	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public void addStates(final List<DerivedStateId> states) {
		for (DerivedStateId derivedStateId : states) {
			addVertex(derivedStateId);
		}
	}

	public List<DerivedOp> addTransitions(final List<DerivedOp> ops) {
		List<DerivedOp> newOps = new ArrayList<DerivedOp>();
		for (DerivedOp op : ops) {
			if (!containsEdge(op)) {
				DerivedOp d = new DerivedOp((idGenerator++) + "", op.getSrc(),
						op.getDest(), op.getRep(), op.getColor(), op.getStyle());
				newOps.add(d);
				addEdge(d, states.get(op.getSrc()), states.get(op.getDest()));
			}
		}
		return newOps;
	}

	@Override
	public StateSpaceGraph getSSGraph() {
		return this;
	}

	@Override
	public void execute(final ICommand command) {
		stateSpace.execute(command);
	}

	@Override
	public void execute(final ICommand... commands) {
		stateSpace.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		stateSpace.sendInterrupt();
	}
}
