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
	protected final String sessionId;

	public ProBAnimation(String sessionId, AbstractModel model,
			AnimationSelector animations, ToolRegistry toolRegistry) {
		this(sessionId, animations, toolRegistry);
		this.model = model;
		this.trace = new Trace(model);
		animations.addNewAnimation(this.trace);
	}

	public ProBAnimation(String sessionId, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		this.sessionId = sessionId;
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
	
}
