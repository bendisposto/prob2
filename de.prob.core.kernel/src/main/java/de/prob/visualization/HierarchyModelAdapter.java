package de.prob.visualization;

import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DirectedMultigraph;

import de.prob.model.classicalb.ClassicalBMachine;
import de.prob.model.classicalb.RefType;

@SuppressWarnings("serial")
public class HierarchyModelAdapter extends
		JGraphModelAdapter<ClassicalBMachine, RefType> {

	public HierarchyModelAdapter(
			final DirectedMultigraph<ClassicalBMachine, RefType> graph) {
		super(graph);
	}
}
