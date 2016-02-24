package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.eventb.algorithm.IdentifierExtractor
import de.prob.model.representation.ModelElementList
import de.prob.unicode.UnicodeTranslator

class Assignment extends Statement implements IAssignment {
	EventB assignment

	def Assignment(String assignment, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.assignment = parseAssignment(assignment)
	}

	def String toString() {
		UnicodeTranslator.toUnicode(assignment.getCode())
	}
}
