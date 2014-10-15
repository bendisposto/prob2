package de.prob.bmotion;

import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.StateId;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.ui.api.ITool;
import de.prob.ui.api.ToolRegistry;

public abstract class ProBAnimation implements ITool, IAnimationChangeListener,
		IObserver {

	private AbstractModel model;

	protected StateId currentState;
	protected final ToolRegistry toolRegistry;
	protected final AnimationSelector animations;
	protected final String toolId;

	public ProBAnimation(final String sessionId, final AbstractModel model,
			final AnimationSelector animations, final ToolRegistry toolRegistry) {
		this(sessionId, animations, toolRegistry);
		this.model = model;
		this.currentState = model.getStateSpace().getRoot();
	}

	public ProBAnimation(final String toolId,
			final AnimationSelector animations, final ToolRegistry toolRegistry) {
		this.toolId = toolId;
		this.animations = animations;
		this.toolRegistry = toolRegistry;
		animations.registerAnimationChangeListener(this);
	}

	public AbstractModel getModel() {
		return model;
	}

	public void setModel(final AbstractModel model) {
		this.model = model;
		this.currentState = model.getStateSpace().getRoot();
	}

	public StateSpace getStateSpace() {
		return currentState != null ? currentState.getStateSpace() : null;
	}

	public ToolRegistry getToolRegistry() {
		return toolRegistry;
	}

	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		StateId oldState = currentState;
		if (currentTrace == null) {
			currentState = null;
		}
		if (oldState != null && !currentState.equals(oldState)) {
			toolRegistry.notifyToolChange(this);
		}
	}

	@Override
	public String getCurrentState() {
		return currentState != null ? currentState.getId() : null;
	}

	@Override
	public String getName() {
		return toolId;
	}

	@Override
	public boolean canBacktrack() {
		return true;
	}

}
