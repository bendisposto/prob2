package de.prob.model.eventb.algorithm

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException

class Assertion extends Statement {
	def EventB assertion

	def Assertion(String assertion, Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) throws ModelGenerationException {
		super(typeEnvironment)
		this.assertion = parsePredicate(assertion)
	}

	def String toString() {
		"assert ${assertion.toUnicode()}"
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof Assertion) {
			if (assertion != null) {
				return this.assertion.getCode().equals(that.getAssertion().getCode())
			} else {
				if (that.getAssertion() == null) {
					return true
				}
			}
		}
		return false
	}

	@Override
	public int hashCode() {
		return this.assertion.hashCode();
	}
}
