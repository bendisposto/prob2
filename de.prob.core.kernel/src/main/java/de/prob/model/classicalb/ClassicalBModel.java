package de.prob.model.classicalb;

import java.io.File;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.analysis.prolog.RecursiveMachineLoader;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.MultiGraph;

public class ClassicalBModel extends ClassicalBModelGraph implements
		DirectedGraph<ClassicalBMachine, ClassicalBDependencyType>,
		MultiGraph<ClassicalBMachine, ClassicalBDependencyType> {

	private final StateSpace statespace;
	private ClassicalBMachine mainMachine = null;

	@Inject
	public ClassicalBModel(
			final DirectedSparseMultigraph<ClassicalBMachine, ClassicalBDependencyType> graph,
			StateSpace statespace) {
		super(graph);
		this.statespace = statespace;
	}

	public void initialize(Start ast,File f) throws ProBException {

//		NodeIdAssignment nodeIdMapping = ast.getNodeIdMapping();
//		ClassicalBMachine classicalBMachine = new ClassicalBMachine(
//				nodeIdMapping);
//
//		addVertex(classicalBMachine);
//		mainMachine = classicalBMachine;

	}

	public StateSpace getStatespace() {
		return statespace;
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

}
