package de.prob.model.eventb.algorithm.graph;

import java.util.ArrayList;
import java.util.List;

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions;

public class GraphTransformer implements IGraphTransformer {
	private List<IGraphTransformer> transformers;
	
	public GraphTransformer(AlgorithmGenerationOptions options) {
		transformers = new ArrayList<>();
		if (options.isMergeBranches()) {
			transformers.add(new MergeConditionals());
		}

		if (options.isOptimize()) {
			transformers.add(new MergeAssignment());
		}

	}

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		ControlFlowGraph g = graph;
		for (IGraphTransformer t : transformers) {
			g = t.transform(g);
		}
		return g;
	}
}
