package de.prob.model.eventb.newdom;

import java.util.List;

import de.prob.model.representation.newdom.AbstractElement;
import de.prob.model.representation.newdom.Machine;

public class EBModel extends AbstractElement {

	public void addMachines(final List<EventBMachine> machines) {
		put(Machine.class, machines);
	}

	public void addContexts(final List<Context> contexts) {
		put(Context.class, contexts);
	}
}
