package de.prob.visualization;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.classicalb.RefType;

@SuppressWarnings("serial")
public class HierarchyGraph extends JGraph {

	private final ClassicalBModel model;

	public HierarchyGraph(
			final JGraphModelAdapter<ClassicalBMachine, RefType> adapter,
			final ClassicalBModel model) {
		super(adapter);
		this.model = model;
	}

}
