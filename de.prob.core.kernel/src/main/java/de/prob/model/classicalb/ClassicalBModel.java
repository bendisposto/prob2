package de.prob.model.classicalb;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;

public class ClassicalBModel extends ClassicalBModelGraph implements
		DirectedGraph<ClassicalBMachine, ClassicalBDependencyType>,
		MultiGraph<ClassicalBMachine, ClassicalBDependencyType> {

	public ClassicalBModel(
			final DirectedSparseMultigraph<ClassicalBMachine, ClassicalBDependencyType> graph) {
		super(graph);
	}

}
