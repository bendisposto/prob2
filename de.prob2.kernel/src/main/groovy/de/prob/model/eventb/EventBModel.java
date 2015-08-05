package de.prob.model.eventb;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;
import com.google.inject.Inject;

import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.DependencyGraph.ERefType;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	@Inject
	public EventBModel(final StateSpaceProvider stateSpaceProvider) {
		this(stateSpaceProvider, PersistentHashMap.<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>>emptyMap(), new DependencyGraph(),
				null);
	}

	private EventBModel(StateSpaceProvider stateSpaceProvider, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children,
			DependencyGraph graph, File modelFile) {
		super(stateSpaceProvider, children, graph, modelFile);
	}

	public ModelElementList<Machine> getMachines() {
		return getChildrenOfType(Machine.class);
	}

	public ModelElementList<Context> getContexts() {
		return getChildrenOfType(Context.class);
	}

	public EventBModel setModelFile(final File modelFile) {
		return new EventBModel(stateSpaceProvider, children, graph, modelFile);
	}

	@Override
	public IEvalElement parseFormula(final String formula) {
		return new EventB(formula);
	}

	public EventBModel set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new EventBModel(stateSpaceProvider, assoc(clazz, elements), graph, modelFile);
	}

	public <T extends AbstractElement, S extends T> EventBModel addTo(Class<T> clazz, S element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(stateSpaceProvider, assoc(clazz, list.addElement(element)), graph, modelFile);
	}

	public <T extends AbstractElement, S extends T> EventBModel removeFrom(Class<T> clazz, S element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(stateSpaceProvider, assoc(clazz, list.removeElement(element)), graph, modelFile);
	}

	public <T extends AbstractElement, S extends T> EventBModel replaceIn(Class<T> clazz, S oldElement, S newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(stateSpaceProvider, assoc(clazz, list.replaceElement(oldElement, newElement)), graph, modelFile);
	}

	public EventBModel addMachine(final EventBMachine machine) {
		ModelElementList<Machine> list = getChildrenOfType(Machine.class);
		return new EventBModel(stateSpaceProvider, assoc(Machine.class, list.addElement(machine)), graph.addVertex(machine.getName()), modelFile);
	}

	public EventBModel addContext(final Context context) {
		ModelElementList<Context> list = getChildrenOfType(Context.class);
		return new EventBModel(stateSpaceProvider, assoc(Context.class, list.addElement(context)), graph.addVertex(context.getName()),
				modelFile);
	}

	public EventBModel addRelationship(final String element1, final String element2,
			final ERefType relationship) {
		return new EventBModel(stateSpaceProvider, children, graph.addEdge(element1, element2, relationship),modelFile);
	}

	@Override
	public ERefType getRelationship(final String from, final String to) {
		List<ERefType> relationships = graph.getRelationships(from, to);
		return relationships.isEmpty() ? null : relationships.get(0);
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
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

	@Override
	public AbstractElement getComponent(String name) {
		AbstractElement e = getChildrenOfType(Context.class).getElement(name);
		if (e == null) {
			e = getChildrenOfType(Machine.class).getElement(name);
		}
		return e;
	}

}
