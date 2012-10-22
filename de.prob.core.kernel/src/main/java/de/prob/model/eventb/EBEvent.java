package de.prob.model.eventb;

import java.util.Arrays;

import org.eventb.emf.core.machine.Action;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Guard;
import org.eventb.emf.core.machine.Parameter;
import org.eventb.emf.core.machine.Witness;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class EBEvent extends EventBElement {

	final public Label refines = new Label("REFINES");
	final public Label parameters = new Label("ANY");
	final public Label guards = new Label("WHERE");
	final public Label witnesses = new Label("WITH");
	final public Label actions = new Label("THEN");

	public EBEvent(final Event event) {
		super(event);

		for (final Event event2 : event.getRefines()) {
			refines.addChild(new Label(event2.doGetName()));
		}

		for (final Parameter parameter : event.getParameters()) {
			parameters.addChild(new EventB(parameter.doGetName()));
		}

		for (final Guard guard : event.getGuards()) {
			guards.addChild(new EventB(guard.getPredicate()));
		}

		for (final Witness witness : event.getWitnesses()) {
			witnesses.addChild(new EventB(witness.getPredicate()));
		}

		for (final Action action : event.getActions()) {
			actions.addChild(new Label(action.getAction()));
		}

		children.addAll(Arrays.asList(new IEntity[] { refines, parameters,
				guards, witnesses, actions }));
	}

	@Override
	public String toString() {
		return name;
	}
}
