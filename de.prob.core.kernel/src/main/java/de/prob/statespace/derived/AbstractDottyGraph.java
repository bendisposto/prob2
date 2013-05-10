package de.prob.statespace.derived;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.prob.animator.command.AbstractCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;
import de.prob.statespace.StateSpaceGraph;

public abstract class AbstractDottyGraph implements IStateSpace,
		IStatesCalculatedListener {

	protected StateSpace space;
	protected Set<IStatesCalculatedListener> listeners = new HashSet<IStatesCalculatedListener>();
	protected String content;

	public AbstractDottyGraph(final IStateSpace space) {
		if (space instanceof StateSpace) {
			this.space = (StateSpace) space;
		} else if (space instanceof AbstractDerivedStateSpace) {
			this.space = ((AbstractDerivedStateSpace) space).getStateSpace();
		} else if (space instanceof AbstractDottyGraph) {
			this.space = ((AbstractDottyGraph) space).getStateSpace();
		} else {
			throw new UnsupportedOperationException(
					"Could not create AbstractDerivedStateSpace because the instance of IStateSpace was not recognized");
		}
		this.space.registerStateSpaceListener(this);
	}

	protected abstract void calculate();

	@Override
	public void execute(final AbstractCommand command) {
		space.execute(command);
	}

	@Override
	public void execute(final AbstractCommand... commands) {
		space.execute(commands);
	}

	@Override
	public void sendInterrupt() {
		space.sendInterrupt();
	}

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
		return space;
	}

	@Override
	public StateSpaceGraph getSSGraph() {
		return space;
	}

	public String getContent() {
		return content;
	}

}
