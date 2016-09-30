package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.If
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class AddLoopEvents extends AlgorithmASTTransformer {

	def final boolean addLoopByDefault

	def AddLoopEvents(AlgorithmGenerationOptions options) {
		addLoopByDefault = options.isLoopEvent()
	}

	def AddLoopEvents() {
		addLoopByDefault = false
	}

	@Override
	public List<Statement> transform(While w, List<Statement> rest) {
		Block b = w.block
		if (addSkip(w)) {
			b = b.finish()
		}
		recurIfNecessary(w.updateBlock(transform(b)), rest);
	}

	@Override List<Statement> transform(If i, List<Statement> rest) {
		Block t = i.Then
		if (t.statements && t.statements.last() instanceof Assertion) {
			t = t.finish()
		}
		Block e = i.Else
		if (e.statements && e.statements.last() instanceof Assertion) {
			e = e.finish()
		}
		recurIfNecessary(i.newIf(transform(t),transform(e)), rest)
	}

	def boolean addSkip(While w) {
		if (addLoopByDefault) {
			return true
		}
		return w.block.statements && w.block.statements.last() instanceof Assertion
	}
}
