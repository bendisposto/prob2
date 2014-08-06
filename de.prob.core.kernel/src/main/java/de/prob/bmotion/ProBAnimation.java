package de.prob.bmotion;

import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IAnimationChangeListener;
import de.prob.statespace.Trace;
import de.prob.ui.api.ITool;
import de.prob.ui.api.ToolRegistry;

public abstract class ProBAnimation implements ITool, IAnimationChangeListener,
		IObserver {

	private AbstractModel model;

	protected Trace trace;
	protected final ToolRegistry toolRegistry;
	protected final AnimationSelector animations;
	protected final String toolId;

	public ProBAnimation(String sessionId, AbstractModel model,
			AnimationSelector animations, ToolRegistry toolRegistry) {
		this(sessionId, animations, toolRegistry);
		this.model = model;
		this.trace = new Trace(model);
	}

	public ProBAnimation(String toolId, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		this.toolId = toolId;
		this.animations = animations;
		this.toolRegistry = toolRegistry;
		animations.registerAnimationChangeListener(this);
	}

	public AbstractModel getModel() {
		return model;
	}
	
	public void setModel(AbstractModel model) {
		this.model = model;
		this.trace = new Trace(model);
	}

	public Trace getTrace() {
		return trace;
	}

	public ToolRegistry getToolRegistry() {
		return toolRegistry;
	}
	
	@Override
	public void traceChange(final Trace currentTrace,
			final boolean currentAnimationChanged) {
		Trace oldtrace = trace;
		trace = currentTrace;
		if (oldtrace != null && !currentTrace.equals(oldtrace)) {
			toolRegistry.notifyToolChange(this);
		}
	}
	
	@Override
	public String getCurrentState() {
		return trace != null ? trace.getCurrentState().getId() : null;
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
