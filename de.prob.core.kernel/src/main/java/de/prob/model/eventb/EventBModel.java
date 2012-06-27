package de.prob.model.eventb;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;

import com.google.inject.Inject;

import de.prob.statespace.StateSpace;

public class EventBModel {

	private final StateSpace statespace;

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public StateSpace getStatespace() {
		return statespace;
	}

	public void initialize(final Project p) {
		EList<EventBNamedCommentedComponentElement> components = p
				.getComponents();
		for (EventBNamedCommentedComponentElement element : components) {
			System.out.println(element);
		}
	}
}
