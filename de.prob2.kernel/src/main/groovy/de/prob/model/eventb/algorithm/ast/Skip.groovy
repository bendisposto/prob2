package de.prob.model.eventb.algorithm.ast

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * the equivalent of an empty assignment.
 * @author joy
 *
 */
class Skip extends Statement implements IAssignment {

	def Skip(Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
	}

	@Override
	public String toString() {
		return "skip"
	}
}
