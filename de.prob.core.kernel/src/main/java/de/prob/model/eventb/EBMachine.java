package de.prob.model.eventb;

import java.util.Arrays;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Invariant;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.core.machine.Variable;
import org.eventb.emf.core.machine.Variant;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;

public class EBMachine extends EventBElement {

	final public Label variables = new Label("Variables");
	final public Label invariants = new Label("Invariants");
	final public Label variant = new Label("Variant");
	final public Label events = new Label("Events");

	public EBMachine(final Machine machine) {
		super(machine);

		for (final Variable variable : machine.getVariables()) {
			variables.addChild(new EventB(variable.doGetName()));
		}

		for (final Invariant invariant : machine.getInvariants()) {
			invariants.addChild(new EventB(invariant.getPredicate()));
		}

		final Variant variant2 = machine.getVariant();
		if (variant2 != null) {
			final String expression = variant2.getExpression();
			final EventB child = new EventB(expression);
			variant.addChild(child);
		}

		final EList<Event> events2 = machine.getEvents();
		for (final Event event : events2) {
			events.addChild(new EBEvent(event));
		}

		children.addAll(Arrays.asList(new IEntity[] { variables, invariants,
				variant, events }));
	}
}
