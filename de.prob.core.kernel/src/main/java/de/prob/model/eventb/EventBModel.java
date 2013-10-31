package de.prob.model.eventb;

import java.io.File;
import java.util.List;

import com.google.inject.Inject;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;
import de.prob.model.representation.StateSchema;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	private AbstractElement mainComponent;
	private final BStateSchema schema = new BStateSchema();
	private final ModelElementList<EventBMachine> machines = new ModelElementList<EventBMachine>();
	private final ModelElementList<Context> contexts = new ModelElementList<Context>();

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	@Override
	public StateSchema getStateSchema() {
		return schema;
	}

	public void addMachines(final ModelElementList<EventBMachine> collection) {
		put(Machine.class, collection);
	}

	public void addContexts(final ModelElementList<Context> contexts) {
		put(Context.class, contexts);
	}

	public void setModelFile(final File modelFile) {
		this.modelFile = modelFile;
	}

	public void isFinished() {
		addMachines(machines);
		addContexts(contexts);
		statespace.setModel(this);
	}

	public void setMainComponent(final AbstractElement mainComponent) {
		this.mainComponent = mainComponent;
	}

	@Override
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

	public void calculateGraph() {
		for (Machine machine : getChildrenOfType(Machine.class)) {
			graph.addVertex(machine.getName());
			components.put(machine.getName(), machine);
		}
		for (Context context : getChildrenOfType(Context.class)) {
			graph.addVertex(context.getName());
			components.put(context.getName(), context);
		}

		for (Machine machine : getChildrenOfType(Machine.class)) {
			for (Machine refinement : machine.getChildrenOfType(Machine.class)) {
				graph.addEdge(new RefType(ERefType.REFINES), machine.getName(),
						refinement.getName());
			}
			for (Context seen : machine.getChildrenOfType(Context.class)) {
				graph.addEdge(new RefType(ERefType.SEES), machine.getName(),
						seen.getName());
			}
		}
		List<Context> contexts = getChildrenOfType(Context.class);
		for (Context context : contexts) {
			for (Context seen : context.getChildrenOfType(Context.class)) {
				graph.addEdge(new RefType(ERefType.EXTENDS), context.getName(),
						seen.getName());
			}
		}
	}

	@Override
	public IEvalElement parseFormula(final String formula) {
		return new EventB(formula);
	}

	public void addMachine(final EventBMachine machine) {
		graph.addVertex(machine.getName());
		components.put(machine.getName(), machine);
		machines.add(machine);
	}

	public void addContext(final Context context) {
		graph.addVertex(context.getName());
		components.put(context.getName(), context);
		contexts.add(context);
	}

	public void addRelationship(final String element1, final String element2,
			final RefType relationship) {
		graph.addVertex(element1);
		graph.addVertex(element2);
		graph.addEdge(relationship, element1, element2);
	}

	public void addTheories(final ModelElementList<Theory> theories) {
		put(Theory.class, theories);
	}

}
