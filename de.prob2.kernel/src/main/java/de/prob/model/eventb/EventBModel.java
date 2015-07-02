package de.prob.model.eventb;

import java.io.File;
import java.util.Map;

import com.google.inject.Inject;

import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.theory.Theory;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	private final ModelElementList<EventBMachine> machines = new ModelElementList<EventBMachine>();
	private final ModelElementList<Context> contexts = new ModelElementList<Context>();

	@Inject
	public EventBModel(final StateSpaceProvider stateSpaceProvider) {
		super(stateSpaceProvider);
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
		freeze();
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
			final ERefType relationship) {
		graph.addVertex(element1);
		graph.addVertex(element2);
		graph.addEdge(element1, element2, relationship);
	}

	public void addTheories(final ModelElementList<Theory> theories) {
		put(Theory.class, theories);
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
	}

	public ModelElementList<EventBMachine> getMachines() {
		return machines;
	}

	public ModelElementList<Context> getContexts() {
		return contexts;
	}

	@Override
	public boolean checkSyntax(final String formula) {
		try {
			EventB element = (EventB) parseFormula(formula);
			element.ensureParsed();
			return true;
		} catch (EvaluationException e) {
			return false;
		}
	}

	@Override
	public StateSpace load(final AbstractElement mainComponent,
			final Map<String, String> preferences) {
		return stateSpaceProvider.loadFromCommand(this, mainComponent,
				preferences, new LoadEventBProjectCommand(
						new EventBModelTranslator(this, mainComponent)));
	}

}
