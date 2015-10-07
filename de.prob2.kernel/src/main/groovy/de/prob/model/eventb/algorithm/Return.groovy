package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension;

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException;

class Return extends Statement {
	List<EventB> returnVals

	def Return(List<String> returnVals, Set<IFormulaExtension> typeEnvironment) throws ModelGenerationException{
		super(typeEnvironment)
		this.returnVals = returnVals.collect { parseIdentifier(it) }
	}
}
