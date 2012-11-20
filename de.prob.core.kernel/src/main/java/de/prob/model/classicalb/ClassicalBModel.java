package de.prob.model.classicalb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.AbstractModel;
import de.prob.model.representation.Machine;
import de.prob.model.representation.RefType;
import de.prob.statespace.StateSpace;

public class ClassicalBModel extends AbstractModel {

	private ClassicalBMachine mainMachine = null;
	private final HashSet<String> done = new HashSet<String>();

	@Inject
	public ClassicalBModel(final StateSpace statespace) {
		this.statespace = statespace;
	}

	public DirectedMultigraph<String, RefType> initialize(final Start mainast,
			final RecursiveMachineLoader rml) {

		final DirectedMultigraph<String, RefType> graph = new DirectedMultigraph<String, RefType>(
				new ClassBasedEdgeFactory<String, RefType>(RefType.class));

		final DomBuilder d = new DomBuilder();
		mainMachine = d.build(mainast);

		graph.addVertex(mainMachine.getName());
		Set<ClassicalBMachine> machines = new LinkedHashSet<ClassicalBMachine>();
		machines.add(mainMachine);

		boolean fpReached;

		do {
			fpReached = true;
			final Set<String> vertices = new HashSet<String>();
			vertices.addAll(graph.vertexSet());
			for (final String machineName : vertices) {
				final Start ast = rml.getParsedMachines().get(machineName);
				if (!done.contains(machineName)) {
					ast.apply(new DependencyWalker(machineName, machines,
							graph, rml.getParsedMachines()));
					done.add(machineName);
					fpReached = false;
				}
			}
		} while (!fpReached);
		this.graph = graph;

		put(Machine.class, machines);

		statespace.setModel(this);
		return graph;
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

	@Override
	public AbstractElement getComponent(final String name) {
		Set<Machine> components = getChildrenOfType(Machine.class);
		for (Machine machine : components) {
			if (machine.getName().equals(name)) {
				return machine;
			}
		}
		return null;
	}

	@Override
	public Map<String, AbstractElement> getComponents() {
		Map<String, AbstractElement> components = new HashMap<String, AbstractElement>();
		for (Machine machine : getChildrenOfType(Machine.class)) {
			components.put(machine.getName(), machine);
		}
		return components;
	}
}
