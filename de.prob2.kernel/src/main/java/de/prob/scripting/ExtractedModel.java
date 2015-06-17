package de.prob.scripting;

import java.util.Collections;
import java.util.Map;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.StateSpace;

public class ExtractedModel<T extends AbstractModel> {
	private final T model;
	private final AbstractElement mainComponent;

	public ExtractedModel(final T model, final AbstractElement mainComponent) {
		this.model = model;
		this.mainComponent = mainComponent;
	}

	public StateSpace load() {
		return load(Collections.<String, String> emptyMap());
	}

	public StateSpace load(final Map<String, String> preferences) {
		return model.load(mainComponent, preferences);
	}

	public T getModel() {
		return model;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}
}
