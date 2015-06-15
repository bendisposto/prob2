package de.prob.scripting;

import java.util.Map;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.StateSpace;

public class ExtractedModel<T extends AbstractModel> {
	private T model;
	private AbstractElement mainComponent;

	public ExtractedModel(T model, AbstractElement mainComponent) {
		this.model = model;
		this.mainComponent = mainComponent;
	}
	
	public StateSpace load(Map<String, String> preferences) {
		return model.load(preferences, mainComponent);
	}
	
	public T getModel() {
		return model;
	}
	
	public AbstractElement getMainComponent() {
		return mainComponent;
	}
}
