package de.prob.model.eventb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Inject;

import de.prob.model.representation.AbstractDomTreeElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	private String mainComponent;

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
		this.components = new HashMap<String, AbstractElement>();
	}

	public void initialize(final Project p, final String mainComponent) {
		this.mainComponent = mainComponent;
		graph = new DirectedMultigraph<String, RefType>(
				new ClassBasedEdgeFactory<String, RefType>(RefType.class));

		final Map<String, EventBNamedCommentedComponentElement> allComponents = new HashMap<String, EventBNamedCommentedComponentElement>();
		EventBNamedCommentedComponentElement element = null;
		for (final EventBNamedCommentedComponentElement cmpt : p
				.getComponents()) {
			final String name = cmpt.doGetName();
			if (mainComponent.equals(name)) {
				element = cmpt;
			}
			allComponents.put(name, cmpt);
		}
		if (element != null) {
			final String name = element.doGetName();
			graph.addVertex(name);
			if (!components.containsKey(name)) {
				components.put(name, new EventBComponent(element));
			}

			if (element instanceof Context) {
				final Context c = (Context) element;
				final EList<Context> ext = c.getExtends();
				for (final Context context : ext) {
					final String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, new EventBComponent(
								allComponents.get(ctxName)));
					}
					graph.addEdge(name, ctxName, new RefType(ERefType.EXTENDS));
				}
			}
			if (element instanceof Machine) {
				final Machine m = (Machine) element;
				final EList<Context> sees = m.getSees();
				for (final Context context : sees) {
					final String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, new EventBComponent(
								allComponents.get(ctxName)));
					}
					graph.addEdge(name, ctxName, new RefType(ERefType.SEES));
				}
				final EList<Machine> refines = m.getRefines();
				for (final Machine machine : refines) {
					final String mName = machine.doGetName();
					if (!components.containsKey(mName)) {
						graph.addVertex(mName);
						components.put(mName,
								new EventBComponent(allComponents.get(mName)));
					}
					graph.addEdge(name, mName, new RefType(ERefType.REFINES));
				}
			}
		}
	}

	public EventBComponent getComponent(final String componentName) {
		return components.containsKey(componentName) ? (EventBComponent) components
				.get(componentName) : null;
	}

	public String getMainComponentName() {
		return mainComponent;
	}

	@Override
	public List<AbstractDomTreeElement> getSubcomponents() {
		Collection<AbstractElement> values = components.values();
		return getSubcomponents(values);
	}
	


}
