package de.prob.model.classicalb;

import java.util.Collection;
import java.util.HashSet;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ClassicalBModel {

	private final StateSpace statespace;
	private ClassicalBMachine mainMachine = null;
	private HashSet<String> done = new HashSet<String>();
	private DirectedSparseMultigraph<String, RefType> graph;

	@Inject
	public ClassicalBModel(StateSpace statespace) {
		this.statespace = statespace;
	}

	public DirectedSparseMultigraph<String, RefType> initialize(Start mainast,
			RecursiveMachineLoader rml) throws ProBException {

		DirectedSparseMultigraph<String, RefType> graph = new DirectedSparseMultigraph<String, RefType>();

		mainMachine = new ClassicalBMachine(null);
		DomBuilder d = new DomBuilder(mainMachine);
		d.build(mainast);
		String name = mainMachine.getName();
		graph.addVertex(name);

		boolean fpReached;

		do {
			fpReached = true;
			Collection<String> vertices = graph.getVertices();
			for (String machine : vertices) {
				Start ast = rml.getParsedMachines().get(machine);
				if (!done.contains(machine)) {
					ast.apply(new DependencyWalker(machine, graph));
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

}
