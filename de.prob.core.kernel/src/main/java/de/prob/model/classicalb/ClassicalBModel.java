package de.prob.model.classicalb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import de.be4.classicalb.core.parser.node.Start;
import de.prob.ProBException;
import de.prob.statespace.StateSpace;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ClassicalBModel {

	private final StateSpace statespace;
	private ClassicalBMachine mainMachine = null;
	Map<String, ClassicalBMachine> content = new HashMap<String, ClassicalBMachine>();

	@Inject
	public ClassicalBModel(StateSpace statespace) {
		this.statespace = statespace;
	}

	public DirectedSparseMultigraph<String, RefType> initialize(
			Start ast, File f) throws ProBException {

		DirectedSparseMultigraph<String, RefType> graph = new DirectedSparseMultigraph<String, RefType>();

		mainMachine = new ClassicalBMachine(null);
		DomBuilder d = new DomBuilder(mainMachine);
		d.build(ast);

		String name = mainMachine.getName();
		graph.addVertex(name);
		content.put(name, mainMachine);

		ast.apply(new DependencyWalker(name,f.getParentFile(), graph));
		return graph;
	}

	public StateSpace getStatespace() {
		return statespace;
	}

	public ClassicalBMachine getMainMachine() {
		return mainMachine;
	}

}
