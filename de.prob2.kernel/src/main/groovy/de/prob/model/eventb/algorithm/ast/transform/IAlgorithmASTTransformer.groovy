package de.prob.model.eventb.algorithm.ast.transform;

import de.prob.model.eventb.algorithm.ast.Block;


public interface IAlgorithmASTTransformer {
	public Block transform(Block algorithm);
}
