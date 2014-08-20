package de.prob.statespace.derived;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.prob.animator.command.AbstractCommand;
import de.prob.statespace.IStateSpace;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.OpInfo;
import de.prob.statespace.StateSpace;

public abstract class AbstractDottyGraph implements IStateSpace,
		IStatesCalculatedListener {

	protected StateSpace space;
	protected Set<IStatesCalculatedListener> listeners = new HashSet<IStatesCalculatedListener>();
	protected String content;
	private boolean registered;

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
			space.registerStateSpaceListener(this);
			registered = true;
		}
		listeners.add(l);
	}

	@Override
	public void deregisterStateSpaceListener(final IStatesCalculatedListener l) {
		listeners.remove(l);
		if (listeners.isEmpty()) {
			space.deregisterStateSpaceListener(this);
			registered = false;
		}
	}

	public StateSpace getStateSpace() {
		return space;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean isBusy() {
		return space.isBusy();
	}

	@Override
	public void startTransaction() {
		space.startTransaction();
	}

	@Override
	public void endTransaction() {
		space.endTransaction();
	}

}
