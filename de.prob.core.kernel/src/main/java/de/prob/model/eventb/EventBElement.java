package de.prob.model.eventb;

import org.eventb.emf.core.EventBNamedCommentedElement;

import de.prob.model.representation.Label;

public abstract class EventBElement extends Label {

	private final EventBNamedCommentedElement emfComponent;

	public EventBElement(final EventBNamedCommentedElement emfComponent) {
		super(emfComponent.doGetName());
		this.emfComponent = emfComponent;
	}

	public EventBNamedCommentedElement getEmfComponent() {
		return emfComponent;
	}
}
