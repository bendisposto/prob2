package de.prob.bmotion;

import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.ui.api.ToolRegistry;

public class ProBAnimation {

	private AbstractModel model;

	protected Trace trace;
	protected final String modelPath;
	protected final ToolRegistry toolRegistry;
	protected final AnimationSelector animations;

	public ProBAnimation(AbstractModel model, AnimationSelector animations,
			ToolRegistry toolRegistry) {
		this.model = model;
		this.modelPath = model.getModelFile().getAbsolutePath();
		this.animations = animations;
		this.toolRegistry = toolRegistry;
		this.trace = new Trace(model);
	}

	public AbstractModel getModel() {
		return model;
	}

	public Trace getTrace() {
		return trace;
	}

	public ToolRegistry getToolRegistry() {
		return toolRegistry;
	}
	
}
