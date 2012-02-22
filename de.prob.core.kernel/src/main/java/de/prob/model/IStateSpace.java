package de.prob.model;

import de.prob.animator.IAnimator;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.MultiGraph;

public interface IStateSpace extends IAnimator,
		DirectedGraph<String, Operation>, MultiGraph<String, Operation> {

}