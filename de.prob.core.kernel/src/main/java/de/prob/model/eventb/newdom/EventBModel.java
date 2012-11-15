package de.prob.model.eventb.newdom;

import java.util.Set;

import com.google.inject.Inject;

import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;
import de.prob.model.representation.newdom.AbstractElement;
import de.prob.model.representation.newdom.AbstractModel;
import de.prob.model.representation.newdom.Machine;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	private AbstractElement mainComponent;

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public void addMachines(final Set<EventBMachine> machines) {
		put(Machine.class, machines);
	}

	public void addContexts(final Set<Context> contexts) {
		put(Context.class, contexts);
	}

	public void isFinished() {
		calculateGraph();
		statespace.setModel(this);
	}

	public void setMainComponent(final AbstractElement mainComponent) {
		this.mainComponent = mainComponent;
	}

	public AbstractElement getMainComponent() {
		return mainComponent;
	}

	public String getMainComponentName() {
		if (mainComponent instanceof Context) {
			return ((Context) mainComponent).getName();
		}
		if (mainComponent instanceof Machine) {
			return ((Machine) mainComponent).getName();
		}
		return "";
	}

	@Override
	public AbstractElement getComponent(final String name) {
		for (Machine machine : getChildrenOfType(Machine.class)) {
			if (machine.getName().equals(name)) {
				return machine;
			}
		}
		for (Context context : getChildrenOfType(Context.class)) {
			if (context.getName().equals(name)) {
				return context;
			}
		}
		return null;
	}

	public void calculateGraph() {
		for (Machine machine : getChildrenOfType(Machine.class)) {
			graph.addVertex(machine.getName());
		}
		for (Context context : getChildrenOfType(Context.class)) {
			graph.addVertex(context.getName());
		}

		for (Machine machine : getChildrenOfType(Machine.class)) {
			for (Machine refinement : machine.getChildrenOfType(Machine.class)) {
				graph.addEdge(machine.getName(), refinement.getName(),
						new RefType(ERefType.REFINES));
			}
			for (Context seen : machine.getChildrenOfType(Context.class)) {
				graph.addEdge(machine.getName(), seen.getName(), new RefType(
						ERefType.SEES));
			}
		}
		Set<Context> contexts = getChildrenOfType(Context.class);
		for (Context context : contexts) {
			for (Context seen : context.getChildrenOfType(Context.class)) {
				graph.addEdge(context.getName(), seen.getName(), new RefType(
						ERefType.EXTENDS));
			}
		}
	}
}
