package de.prob.model.classicalb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.RefType;
import de.prob.statespace.StateSpace;

public class ClassicalBModel extends AbstractModel {

	private ClassicalBMachine mainMachine = null;
	private final HashSet<String> done = new HashSet<String>();

	@Inject
	public ClassicalBModel(final StateSpace statespace) {
		this.statespace = statespace;
		this.components = new HashMap<String, AbstractElement>();
	}

	public DirectedMultigraph<String, RefType> initialize(final Start mainast,
			final RecursiveMachineLoader rml) {

		DirectedMultigraph<String, RefType> graph = new DirectedMultigraph<String, RefType>(
				new ClassBasedEdgeFactory<String, RefType>(RefType.class));

		mainMachine = new ClassicalBMachine(null);
		DomBuilder d = new DomBuilder(mainMachine);
		d.build(mainast);
		graph.addVertex(mainMachine.name());
		components.put(mainMachine.name(), mainMachine);

		boolean fpReached;

		do {
			fpReached = true;
			Set<String> vertices = new HashSet<String>();
			vertices.addAll(graph.vertexSet());
			for (String machineName : vertices) {
				Start ast = rml.getParsedMachines().get(machineName);
				if (!done.contains(machineName)) {
					ast.apply(new DependencyWalker(machineName, components,
							graph, rml.getParsedMachines()));
					done.add(machineName);
					fpReached = false;
				}
			}
		} while (!fpReached);
		this.graph = graph;
		return graph;
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

	public ClassicalBMachine getMachine(final String machineName) {
		return components.containsKey(machineName) ? (ClassicalBMachine) components
				.get(machineName) : null;
	}
}
