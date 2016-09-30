package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Return extends Statement implements IAssignment {
	List<EventB> returnVals

	def Return(List<String> returnVals, Set<IFormulaExtension> typeEnvironment) throws ModelGenerationException{
		super(typeEnvironment)
		this.returnVals = returnVals.collect { parseIdentifier(it) }
	}

	@Override
	public String toString() {
		return "return "+returnVals.collect { it.getCode() }.iterator().join(",")
	}
}
