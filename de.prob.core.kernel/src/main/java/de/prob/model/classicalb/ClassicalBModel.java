package de.prob.model.classicalb;

import java.util.Collection;
import java.util.HashSet;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.model.classicalb.RefType.ERefType;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ClassicalBModel {

	private final StateSpace statespace;
	private ClassicalBMachine mainMachine = null;
	private final HashSet<ClassicalBMachine> done = new HashSet<ClassicalBMachine>();
	private DirectedSparseMultigraph<ClassicalBMachine, RefType> graph;

	@Inject
	public ClassicalBModel(StateSpace statespace) {
		this.statespace = statespace;
	}

	public DirectedSparseMultigraph<ClassicalBMachine, RefType> initialize(
			Start mainast, RecursiveMachineLoader rml) throws ProBException {

		DirectedSparseMultigraph<ClassicalBMachine, RefType> graph = new DirectedSparseMultigraph<ClassicalBMachine, RefType>();

		mainMachine = new ClassicalBMachine(null);
		DomBuilder d = new DomBuilder(mainMachine);
		d.build(mainast);
		graph.addVertex(mainMachine);

		boolean fpReached;

		do {
			fpReached = true;
			Collection<ClassicalBMachine> vertices = graph.getVertices();
			for (ClassicalBMachine machine : vertices) {
				Start ast = rml.getParsedMachines().get(machine.name());
				if (!done.contains(machine)) {
					ast.apply(new DependencyWalker(machine, graph, rml
							.getParsedMachines()));
					done.add(machine);
					fpReached = false;
				}
			}
		} while (!fpReached);
		this.graph = graph;
		return graph;
	}

	public StateSpace getStatespace() {
		return statespace;
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

	public DirectedSparseMultigraph<ClassicalBMachine, RefType> getGraph() {
		return graph;
	}

	public ClassicalBMachine getMachine(final String machineName) {
		return getVertex(machineName);
	}

	public ERefType getRelationship(final String machine1, final String machine2) {
		return getEdge(machine1, machine2);
	}

	public ClassicalBMachine getVertex(final String machineName) {
		final Collection<ClassicalBMachine> vertices = graph.getVertices();
		for (ClassicalBMachine classicalBMachine : vertices) {
			if (classicalBMachine.name().equals(machineName))
				return classicalBMachine;
		}
		return null;
	}

	public ERefType getEdge(final String machine1, final String machine2) {
		final ClassicalBMachine m1 = getVertex(machine1);
		final ClassicalBMachine m2 = getVertex(machine2);

		if (m1 == null || m2 == null)
			return null;

		final RefType edge = graph.findEdge(m1, m2);
		if (edge == null)
			return null;

		return edge.getRelationship();
	}

}
