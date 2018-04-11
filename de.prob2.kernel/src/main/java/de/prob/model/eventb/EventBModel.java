package de.prob.model.eventb;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.krukow.clj_lang.PersistentHashMap;

import com.google.inject.Inject;

import de.prob.animator.command.LoadEventBProjectCommand;
import de.prob.animator.domainobjects.EvaluationException;
import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.FormulaExpand;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.DependencyGraph;
import de.prob.model.representation.Machine;
import de.prob.model.representation.ModelElementList;
import de.prob.scripting.StateSpaceProvider;
import de.prob.statespace.FormalismType;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {
	@Inject
	public EventBModel(final StateSpaceProvider stateSpaceProvider) {
		this(stateSpaceProvider, PersistentHashMap.emptyMap(), new DependencyGraph(), null);
	}

	private EventBModel(StateSpaceProvider stateSpaceProvider, PersistentHashMap<Class<? extends AbstractElement>, ModelElementList<? extends AbstractElement>> children, DependencyGraph graph, File modelFile) {
		super(stateSpaceProvider, children, graph, modelFile);
	}

	public ModelElementList<Machine> getMachines() {
		return getChildrenOfType(Machine.class);
	}

	public ModelElementList<Context> getContexts() {
		return getChildrenOfType(Context.class);
	}

	public EventBModel setModelFile(final File modelFile) {
		return new EventBModel(getStateSpaceProvider(), children, getGraph(), modelFile);
	}

	@Override
	public IEvalElement parseFormula(final String formula, final FormulaExpand expand) {
		return new EventB(formula, Collections.emptySet(), expand);
	}

	public EventBModel set(Class<? extends AbstractElement> clazz, ModelElementList<? extends AbstractElement> elements) {
		return new EventBModel(getStateSpaceProvider(), assoc(clazz, elements), getGraph(), getModelFile());
	}

	public <T extends AbstractElement> EventBModel addTo(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(getStateSpaceProvider(), assoc(clazz, list.addElement(element)), getGraph(), getModelFile());
	}

	public <T extends AbstractElement> EventBModel removeFrom(Class<T> clazz, T element) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(getStateSpaceProvider(), assoc(clazz, list.removeElement(element)), getGraph(), getModelFile());
	}

	public <T extends AbstractElement> EventBModel replaceIn(Class<T> clazz, T oldElement, T newElement) {
		ModelElementList<T> list = getChildrenOfType(clazz);
		return new EventBModel(getStateSpaceProvider(), assoc(clazz, list.replaceElement(oldElement, newElement)), getGraph(), getModelFile());
	}

	public EventBModel addMachine(final EventBMachine machine) {
		ModelElementList<Machine> list = getChildrenOfType(Machine.class);
		return new EventBModel(getStateSpaceProvider(), assoc(Machine.class, list.addElement(machine)), getGraph().addVertex(machine.getName()), getModelFile());
	}

	public EventBModel addContext(final Context context) {
		ModelElementList<Context> list = getChildrenOfType(Context.class);
		return new EventBModel(getStateSpaceProvider(), assoc(Context.class, list.addElement(context)), getGraph().addVertex(context.getName()), getModelFile());
	}

	public EventBModel addRelationship(final String element1, final String element2, final DependencyGraph.ERefType relationship) {
		return new EventBModel(getStateSpaceProvider(), children, getGraph().addEdge(element1, element2, relationship), getModelFile());
	}

	public EventBModel removeRelationship(final String element1, final String element2, final DependencyGraph.ERefType relationship) {
		return new EventBModel(getStateSpaceProvider(), children, getGraph().removeEdge(element1, element2, relationship), getModelFile());
	}

	public EventBModel calculateDependencies() {
		DependencyGraph graph = new DependencyGraph();
		for (final Machine m : getMachines()) {
			graph = graph.addVertex(m.getName());
			for (final EventBMachine m2 : ((EventBMachine)m).getRefines()) {
				graph = graph.addEdge(m.getName(), m2.getName(), DependencyGraph.ERefType.REFINES);
			}
			for (final Context c : ((EventBMachine)m).getSees()) {
				graph = graph.addEdge(m.getName(), c.getName(), DependencyGraph.ERefType.SEES);
			}
		}
		for (final Context c : getContexts()) {
			graph = graph.addVertex(c.getName());
			for (final Context c2 : c.getExtends()) {
				graph = graph.addEdge(c.getName(), c2.getName(), DependencyGraph.ERefType.EXTENDS);
			}
		}
		return new EventBModel(getStateSpaceProvider(), children, graph, getModelFile());
	}

	@Override
	public DependencyGraph.ERefType getRelationship(final String from, final String to) {
		final List<DependencyGraph.ERefType> relationships = getGraph().getRelationships(from, to);
		return relationships.isEmpty() ? null : relationships.get(0);
	}

	@Override
	public FormalismType getFormalismType() {
		return FormalismType.B;
	}

	@Override
	public boolean checkSyntax(final String formula) {
		try {
			final EventB element = (EventB)parseFormula(formula, FormulaExpand.TRUNCATE);
			element.ensureParsed();
			return true;
		} catch (EvaluationException e) {
			return false;
		}
	}

	@Override
	public StateSpace load(final AbstractElement mainComponent, final Map<String, String> preferences) {
		return getStateSpaceProvider().loadFromCommand(this, mainComponent, preferences, new LoadEventBProjectCommand(new EventBModelTranslator(this, mainComponent)));
	}

	@Override
	public AbstractElement getComponent(String name) {
		final AbstractElement e = getChildrenOfType(Context.class).getElement(name);
		if (e == null) {
			return getChildrenOfType(Machine.class).getElement(name);
		} else {
			return e;
		}
	}

	public EventBMachine getMachine(String name) {
		return (EventBMachine) getChildrenOfType(Machine.class).getElement(name);
	}

	public Context getContext(String name) {
		return getChildrenOfType(Context.class).getElement(name);
	}

	@Override
	public Object getProperty(String name) {
		final AbstractElement component = getComponent(name);
		if (component != null) {
			return component;
		} else {
			return super.getProperty(name);
		}
	}

	public AbstractElement getAt(String name) {
		return getComponent(name);
	}
}
