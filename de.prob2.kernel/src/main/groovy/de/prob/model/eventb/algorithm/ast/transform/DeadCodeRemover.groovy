package de.prob.model.eventb.algorithm.ast.transform

import java.util.List;

import de.prob.model.eventb.algorithm.ast.Block;
import de.prob.model.eventb.algorithm.ast.If;
import de.prob.model.eventb.algorithm.ast.Return;
import de.prob.model.eventb.algorithm.ast.Statement;
import de.prob.model.eventb.algorithm.ast.While;

class DeadCodeRemover extends AlgorithmASTTransformer {

	@Override
	public List<Statement> transform(Return a, List<Statement> rest) {
		[a]
	}
}
