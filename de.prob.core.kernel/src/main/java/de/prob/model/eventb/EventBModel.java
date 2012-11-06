package de.prob.model.eventb;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eventb.emf.core.EventBNamedCommentedComponentElement;
import org.eventb.emf.core.Project;
import org.eventb.emf.core.context.Context;
import org.eventb.emf.core.machine.Machine;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Label;
import de.prob.model.representation.RefType;
import de.prob.model.representation.RefType.ERefType;
import de.prob.statespace.StateSpace;

public class EventBModel extends AbstractModel {

	private String mainComponent;

	@Inject
	public EventBModel(final StateSpace statespace) {
		this.statespace = statespace;
		this.components = new HashMap<String, Label>();
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
				if (element instanceof Context) {
					final Context c = (Context) element;
					components.put(name, new EBContext(c.doGetName()));
				} else if (element instanceof Machine) {
					final Machine m = (Machine) element;
					components.put(name, new EBMachine(m.doGetName()));
				}
			}

			if (element instanceof Context) {
				final Context c = (Context) element;
				final EList<Context> ext = c.getExtends();
				for (final Context context : ext) {
					final String ctxName = context.doGetName();
					if (!components.containsKey(ctxName)) {
						graph.addVertex(ctxName);
						components.put(ctxName, new EBContext(ctxName));
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
						components.put(ctxName, new EBContext(ctxName));
					}
					graph.addEdge(name, ctxName, new RefType(ERefType.SEES));
				}
				final EList<Machine> refines = m.getRefines();
				for (final Machine machine : refines) {
					final String mName = machine.doGetName();
					if (!components.containsKey(mName)) {
						graph.addVertex(mName);
						components.put(mName, new EBMachine(mName));
					}
					graph.addEdge(name, mName, new RefType(ERefType.REFINES));
				}
			}
		}
		// statespace.setModel(this);
		// testSerialization();
	}

	public Label getComponent(final String componentName) {
		return components.containsKey(componentName) ? (Label) components
				.get(componentName) : null;
	}

	public String getMainComponentName() {
		return mainComponent;
	}

	public void testSerialization() {
		final Model model = new Model();
		for (final RefType edge : graph.edgeSet()) {
			final Label from = components.get(graph.getEdgeSource(edge));
			final Label to = components.get(graph.getEdgeTarget(edge));
			model.relationships.add(new Relationship(from, to));
		}

		final XStream xstream = new XStream(new JettisonMappedXmlDriver());
		final String xml = xstream.toXML(model);
		try {
			final FileWriter fw = new FileWriter("model.xml");
			final BufferedWriter bw = new BufferedWriter(fw);
			bw.write(xml);
			bw.close();
		} catch (final IOException e1) {
			System.out.println("could not create file");
		}
	}
}
