package de.prob.statespace.derived;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.prob.animator.command.AbstractCommand;
import de.prob.animator.command.AbstractReduceStateSpaceCmd;
import de.prob.model.representation.AbstractModel;
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
	public Map<String, Set<DerivedStateId>> nodeColors = new HashMap<String, Set<DerivedStateId>>();
	public Map<String, Set<OpInfo>> transStyle = new HashMap<String, Set<OpInfo>>();
	public Map<String, Set<OpInfo>> transColor = new HashMap<String, Set<OpInfo>>();
	public boolean registered = false;

	public AbstractDerivedStateSpace(final IStateSpace stateSpace,
			final AbstractReduceStateSpaceCmd cmd) {
		super(new DirectedSparseMultigraph<StateId, OpInfo>());
		removeVertex(__root);
		if (stateSpace instanceof StateSpace) {
			this.stateSpace = (StateSpace) stateSpace;
		} else if (stateSpace instanceof AbstractDerivedStateSpace) {
			this.stateSpace = ((AbstractDerivedStateSpace) stateSpace)
					.getStateSpace();
		} else if (stateSpace instanceof AbstractDottyGraph) {
			this.stateSpace = ((AbstractDottyGraph) stateSpace).getStateSpace();
		} else {
			throw new UnsupportedOperationException(
					"Could not create AbstractDerivedStateSpace because the instance of IStateSpace was not recognized");
		}
		setNodeColors(cmd.getNodeColors());
		setTransStyle(cmd.getTransStyle());
		setTransColor(cmd.getTransColor());
	}

	Set<IStatesCalculatedListener> listeners = new HashSet<IStatesCalculatedListener>();

	@Override
	public abstract void newTransitions(final List<OpInfo> newOps);

	@Override
	public void notifyStateSpaceChange(final List<OpInfo> newOps) {
		for (IStatesCalculatedListener l : listeners) {
			l.newTransitions(newOps);
		}
	}

	@Override
	public void registerStateSpaceListener(final IStatesCalculatedListener l) {
		if (!registered) {
			stateSpace.registerStateSpaceListener(this);
			registered = true;
		}
		listeners.add(l);
	}

	@Override
	public void deregisterStateSpaceListener(final IStatesCalculatedListener l) {
		listeners.remove(l);
		if (listeners.isEmpty()) {
			stateSpace.deregisterStateSpaceListener(this);
			registered = false;
		}
	}

	public StateSpace getStateSpace() {
		return stateSpace;
	}

	public void addStates(final List<DerivedStateId> states) {
		for (DerivedStateId derivedStateId : states) {
			if (containsVertex(derivedStateId)) {
				DerivedStateId v = (DerivedStateId) getVertex(derivedStateId
						.getId());
				v.setCount(derivedStateId.getCount());
			} else {
				addVertex(derivedStateId);
			}
		}
	}

	public List<OpInfo> addTransitions(final List<OpInfo> ops) {
		List<OpInfo> newOps = new ArrayList<OpInfo>();
		for (OpInfo op : ops) {
			if (!containsEdge(op)) {
				newOps.add(op);
				addEdge(op, states.get(op.getSrc()), states.get(op.getDest()));
			}
		}
		return newOps;
	}

	@Override
	public void execute(final AbstractCommand command) {
		stateSpace.execute(command);
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		stateSpace.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		stateSpace.sendInterrupt();
	}

	public Map<String, Set<DerivedStateId>> getNodeColors() {
		return nodeColors;
	}

	public Map<String, Set<OpInfo>> getTransColor() {
		return transColor;
	}

	public Map<String, Set<OpInfo>> getTransStyle() {
		return transStyle;
	}

	public void setNodeColors(final Map<String, Set<DerivedStateId>> nodeColors) {
		this.nodeColors = nodeColors;
	}

	public void setTransColor(final Map<String, Set<OpInfo>> transColor) {
		this.transColor = transColor;
	}

	public void setTransStyle(final Map<String, Set<OpInfo>> transStyle) {
		this.transStyle = transStyle;
	}

	@Override
	public boolean isBusy() {
		return stateSpace.isBusy();
	}

	@Override
	public void startTransaction() {
		stateSpace.startTransaction();
	}

	@Override
	public void endTransaction() {
		stateSpace.endTransaction();
	}

	@Override
	public AbstractModel getModel() {
		return stateSpace.getModel();
	}
}
