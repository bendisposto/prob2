package de.prob.model.serialize;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.model.eventb.Context;
import de.prob.model.eventb.EventBMachine;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;

public class ModelObject {
	final Collection<EventBMachine> machines = new ArrayList<EventBMachine>();
	final Collection<Context> contexts;
	final File modelFile;
	final AbstractElement mainComponent;

	public ModelObject(final EventBModel m) {
		List<Machine> ms = m.getChildrenOfType(Machine.class);
		for (Machine machine : ms) {
			if (machine instanceof EventBMachine) {
				machines.add((EventBMachine) machine);
			}
		}
		contexts = m.getChildrenOfType(Context.class);
		modelFile = m.getModelFile();
		mainComponent = m.getMainComponent();
	}

	public Collection<EventBMachine> getMachines() {
		return machines;
	}

	public Collection<Context> getContexts() {
		return contexts;
	}

	public File getModelFile() {
		return modelFile;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}
}
