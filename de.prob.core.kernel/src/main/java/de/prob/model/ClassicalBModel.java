package de.prob.model;

import de.prob.model.representation.ClassicalBMachine;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;

public class ClassicalBModel extends ClassicalBModelGraph
	implements DirectedGraph<ClassicalBMachine, ClassicalBDependencyType>,
	MultiGraph<ClassicalBMachine, ClassicalBDependencyType> {

	public ClassicalBModel(DirectedSparseMultigraph<ClassicalBMachine, ClassicalBDependencyType> graph) {
		super(graph);
	}
	
}
