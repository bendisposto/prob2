package de.prob.model.eventb.algorithm.ast.transform

import de.prob.model.eventb.algorithm.AlgorithmGenerationOptions
import de.prob.model.eventb.algorithm.ast.Assertion
import de.prob.model.eventb.algorithm.ast.Block
import de.prob.model.eventb.algorithm.ast.Skip
import de.prob.model.eventb.algorithm.ast.Statement
import de.prob.model.eventb.algorithm.ast.While

class AddLoopEvents extends AlgorithmASTTransformer {

	def boolean addLoopByDefault
	
	def AddLoopEvents(AlgorithmGenerationOptions options) {
		addLoopByDefault = options.isLoopEvent()
	}
	
	@Override
	public List<Statement> transform(While w, List<Statement> rest) {
		Block b = w.block 
		if (addSkip(w)) {
			w.block.newBlock(w.block.statements.addElement(new Skip()))
		}
		recurIfNecessary(w.updateBlock(transform(b)), rest);
	}
	
	def boolean addSkip(While w) {
		if (addLoopByDefault) {
			return true
		}
		return w.block.statements && w.block.statements instanceof Assertion
	}
	
}
