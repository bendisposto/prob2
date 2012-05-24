package de.prob.visualization;

import org.jgraph.graph.DefaultGraphCell;

import de.prob.model.classicalb.ClassicalBMachine;

public class ModifiedGraphCell extends DefaultGraphCell {

	private final ClassicalBMachine machine;

	public ModifiedGraphCell(final ClassicalBMachine machine) {
		super();
		this.machine = machine;
	}
}
