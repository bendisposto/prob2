package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Return
import de.prob.model.eventb.algorithm.ast.Statement

class DeadCodeRemover extends AlgorithmASTTransformer {

	@Override
	public List<Statement> transform(Return a, List<Statement> rest) {
		[a]
	}
}
