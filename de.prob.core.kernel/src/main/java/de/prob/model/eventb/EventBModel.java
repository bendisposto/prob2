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

		EventBNamedCommentedComponentElement element = null;
		for (EventBNamedCommentedComponentElement cmpt : p.getComponents()) {
			if (mainComponent.equals(cmpt.doGetName())) {
				element = cmpt;
			}
		}
		if (element != null) {
			String name = element.doGetName();
			graph.addVertex(name);
			if (!components.containsKey(name)) {
				components.put(name, new EventBComponent(element));
			}

			if (element instanceof Context) {
				Context c = (Context) element;
				EList<Context> ext = c.getExtends();
				for (Context context : ext) {
					String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, new EventBComponent(context));
					}
					graph.addEdge(name, ctxName, new RefType(ERefType.EXTENDS));
				}
			}
			if (element instanceof Machine) {
				Machine m = (Machine) element;
				EList<Context> sees = m.getSees();
				for (Context context : sees) {
					String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, new EventBComponent(context));
					}
					graph.addEdge(name, ctxName, new RefType(ERefType.SEES));
				}
				EList<Machine> refines = m.getRefines();
				for (Machine machine : refines) {
					String mName = machine.doGetName();
					if (!components.containsKey(mName)) {
						graph.addVertex(mName);
						components.put(mName, new EventBComponent(machine));
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

}
