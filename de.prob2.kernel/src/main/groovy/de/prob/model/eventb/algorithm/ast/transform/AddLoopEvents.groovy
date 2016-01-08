package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While
import de.prob.model.eventb.algorithm.ast.Skip

class AddLoopEvents extends AlgorithmASTTransformer {

	@Override
	public List<Statement> transform(While w, List<Statement> rest) {
		Block b = w.block
		if (w.block.statements.isEmpty()) {
			throw new IllegalArgumentException("While block must not be empty!")
		}
		if (w.block.statements.last() instanceof Assertion) {
			b = w.block.newBlock(w.block.statements.addElement(new Skip()))
		}
		recurIfNecessary(w.updateBlock(transform(b)), rest);
	}
}
