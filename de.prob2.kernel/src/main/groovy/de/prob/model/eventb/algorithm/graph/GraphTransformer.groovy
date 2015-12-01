package de.prob.model.eventb.algorithm.graph

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions.Options;

public class GraphTransformer implements IGraphTransformer {

	List<IGraphTransformer> transformers

	def GraphTransformer(AlgorithmGenerationOptions options) {
		if (options.getOptions().contains(Options.mergeBranches)) {
			transformers = [new GraphMerge()]
		} else {
			transformers = []
		}
	}

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		return transformers.inject(graph) { ControlFlowGraph g, IGraphTransformer t ->
			t.transform(g)
		}
	}
}
