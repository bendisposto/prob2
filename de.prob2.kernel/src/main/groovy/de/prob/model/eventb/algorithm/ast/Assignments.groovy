package de.prob.model.eventb.algorithm.ast

import org.eventb.core.ast.extension.IFormulaExtension

import de.prob.animator.domainobjects.EvalElementType
import de.prob.animator.domainobjects.EventB
import de.prob.model.eventb.ModelGenerationException
import de.prob.model.eventb.algorithm.AssignmentAnalysisVisitor
import de.prob.model.representation.ModelElementList
import de.prob.unicode.UnicodeTranslator

class Assignments extends Statement implements IAssignment {
	ModelElementList<EventB> assignments

	def Assignments(Set<IFormulaExtension> typeEnvironment=Collections.emptySet()) {
		super(typeEnvironment)
		assignments = new ModelElementList<EventB>()
	}

	def Assignments(List<EventB> assignments, Set<IFormulaExtension> typeEnvironment) {
		super(typeEnvironment)
		this.assignments = new ModelElementList<EventB>(assignments)
		this.assignments.each { EventB e ->
			assert e.getKind() == EvalElementType.ASSIGNMENT.toString()
		}
	}

	def String toString() {
		UnicodeTranslator.toUnicode(assignments.collect {it.getCode()}.iterator().join(" || "))
	}

	public Assignments createAssignments(String... assignments) throws ModelGenerationException {
		return new Assignments(assignments.collect {
			parseFormula(it, EvalElementType.ASSIGNMENT)
		}, typeEnvironment)
	}
}
