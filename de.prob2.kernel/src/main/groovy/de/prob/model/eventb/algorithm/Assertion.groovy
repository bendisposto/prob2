package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Assertion extends Statement implements IProperty {
	def EventB assertion

	def Assertion(String assertion, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.assertion = parsePredicate(assertion)
	}

	def String toString() {
		"assert ${assertion.toUnicode()}"
	}

	@Override
	public EventB getFormula() {
		return assertion
	}
}
