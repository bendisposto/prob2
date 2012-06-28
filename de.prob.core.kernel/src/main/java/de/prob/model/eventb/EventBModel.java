package de.prob.model.eventb;

import java.util.HashMap;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Inject;

import de.prob.model.eventb.EventBRefType.EEBRefType;
import de.prob.statespace.StateSpace;

public class EventBModel {

	private final StateSpace statespace;
	private DirectedMultigraph<String, EventBRefType> graph;
	private final HashMap<String, EventBNamedCommentedComponentElement> components = new HashMap<String, EventBNamedCommentedComponentElement>();
	private String mainComponent;

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public StateSpace getStatespace() {
		return statespace;
	}

	public void initialize(final Project p, String mainComponent) {
		this.mainComponent = mainComponent;
		graph = new DirectedMultigraph<String, EventBRefType>(
				new ClassBasedEdgeFactory<String, EventBRefType>(
						EventBRefType.class));

		EList<EventBNamedCommentedComponentElement> cmpnnts = p.getComponents();
		for (EventBNamedCommentedComponentElement element : cmpnnts) {
			String name = element.doGetName();
			graph.addVertex(name);
			if (!components.containsKey(name)) {
				components.put(name, element);
			}

			if (element instanceof Context) {
				Context c = (Context) element;
				EList<Context> ext = c.getExtends();
				for (Context context : ext) {
					String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, context);
					}
					graph.addEdge(name, ctxName, new EventBRefType(
							EEBRefType.EXTENDS));
				}
			}
			if (element instanceof Machine) {
				Machine m = (Machine) element;
				EList<Context> sees = m.getSees();
				for (Context context : sees) {
					String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, context);
					}
					graph.addEdge(name, ctxName, new EventBRefType(
							EEBRefType.SEES));
				}
				EList<Machine> refines = m.getRefines();
				for (Machine machine : refines) {
					String mName = machine.doGetName();
					if (!components.containsKey(mName)) {
						graph.addVertex(mName);
						components.put(mName, machine);
					}
					graph.addEdge(name, mName, new EventBRefType(
							EEBRefType.REFINES));
				}
			}
		}
	}

	public DirectedMultigraph<String, EventBRefType> getGraph() {
		return graph;
	}

	public HashMap<String, EventBNamedCommentedComponentElement> getComponents() {
		return components;
	}

	public EventBNamedCommentedComponentElement getComponent(
			final String componentName) {
		return components.get(componentName);
	}

	public EEBRefType getRelationship(final String comp1, final String comp2) {
		return getEdge(comp1, comp2);
	}

	public EEBRefType getEdge(final String comp1, final String comp2) {
		final EventBRefType edge = graph.getEdge(comp1, comp2);
		if (edge == null)
			return null;

		return edge.getRelationship();
	}

	@Override
	public String toString() {
		return graph.toString();
	}

	public String getMainComponentName() {
		return mainComponent;
	}

}
