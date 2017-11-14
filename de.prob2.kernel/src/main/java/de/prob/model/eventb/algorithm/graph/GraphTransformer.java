package de.prob.model.eventb.algorithm.graph;

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.ArrayList;
import java.util.List;

public class GraphTransformer implements IGraphTransformer {
	public GraphTransformer(AlgorithmGenerationOptions options) {
		transformers = new ArrayList<IGraphTransformer>();
		if (options.isMergeBranches()) {
			DefaultGroovyMethods.leftShift(transformers, new MergeConditionals());
		}

		if (options.isOptimize()) {
			DefaultGroovyMethods.leftShift(transformers, new MergeAssignment());
		}

	}

	@Override
	public ControlFlowGraph transform(ControlFlowGraph graph) {
		return DefaultGroovyMethods.inject(transformers, graph, new Closure<ControlFlowGraph>(this, this) {
			public ControlFlowGraph doCall(ControlFlowGraph g, IGraphTransformer t) {
				return t.transform(g);
			}

		});
	}

	public List<IGraphTransformer> getTransformers() {
		return transformers;
	}

	public void setTransformers(List<IGraphTransformer> transformers) {
		this.transformers = transformers;
	}

	private List<IGraphTransformer> transformers;
}
