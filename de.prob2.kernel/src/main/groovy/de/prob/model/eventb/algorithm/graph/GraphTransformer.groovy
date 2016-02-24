package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions.Options;

public class GraphTransformer implements IGraphTransformer {

	List<IGraphTransformer> transformers

	def GraphTransformer(AlgorithmGenerationOptions options) {
		transformers = []
		if (options.isMergeBranches()) {
			transformers << new MergeConditionals()
		}
		if (options.isOptimize()) {
			transformers << new MergeAssignment()
		}
	}

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		return transformers.inject(graph) { ControlFlowGraph g, IGraphTransformer t ->
			t.transform(g)
		}
	}
}
