package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Assertion extends Statement {
	def EventB assertion

	def Assertion(String assertion, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.assertion = parsePredicate(assertion)
	}

	def Assertion(EventB assertion) {
		super(assertion.getTypes())
		this.assertion = assertion
	}

	def String toString() {
		"assert ${assertion.toUnicode()}"
	}
}
