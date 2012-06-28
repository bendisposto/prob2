package de.prob.model.eventb;

import java.util.List;

import org.eventb.emf.core.EventBNamedCommentedComponentElement;

import de.prob.model.representation.AbstractElement;

public class EventBComponent implements AbstractElement {

	private final EventBNamedCommentedComponentElement emfComponent;

	public EventBComponent(
			final EventBNamedCommentedComponentElement emfComponent) {
		this.emfComponent = emfComponent;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getConstants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getOperations() {
		// TODO Auto-generated method stub
		return null;
	}
}
