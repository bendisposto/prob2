package de.prob.model.classicalb;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;

public class BMachineHierarchy extends
		DirectedMultigraph<ClassicalBMachine, BMachineRelation> {

	public BMachineHierarchy(
			final EdgeFactory<ClassicalBMachine, BMachineRelation> arg0) {
		super(arg0);
	}

	private static final long serialVersionUID = 6236876514397961098L;

}
