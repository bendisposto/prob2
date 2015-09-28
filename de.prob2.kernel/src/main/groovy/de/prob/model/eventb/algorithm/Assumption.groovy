package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Assumption extends Statement implements IProperty  {
	def EventB assumption

	def Assumption(String assumption, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.assumption = parsePredicate(assumption)
	}

	def String toString() {
		"assume ${assumption.toUnicode()}"
	}

	@Override
	public EventB getFormula() {
		return assumption
	}
}
