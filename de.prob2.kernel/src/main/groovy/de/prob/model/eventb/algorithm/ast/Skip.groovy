package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

/**
 * the equivalent of an empty assignment.
 * @author joy
 *
 */
class Skip extends Statement {

	def Skip(Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
	}

	@Override
	public String toString() {
		return "skip"
	}
}
